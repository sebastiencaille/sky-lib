package ch.skymarshall.dataflowmgr.generator.writers.javarx;

import static ch.skymarshall.util.text.TextFormatter.toCamelCase;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ch.skymarshall.dataflowmgr.generator.writers.AbstractJavaFlowVisitor;
import ch.skymarshall.dataflowmgr.generator.writers.FlowGeneratorVisitor;
import ch.skymarshall.dataflowmgr.generator.writers.javarx.AbstractFlowGenerator.GenContext;
import ch.skymarshall.dataflowmgr.model.Binding;
import ch.skymarshall.dataflowmgr.model.ExternalAdapter;
import ch.skymarshall.dataflowmgr.model.Flow;
import ch.skymarshall.util.generators.JavaCodeGenerator;
import ch.skymarshall.util.generators.Template;
import ch.skymarshall.util.helpers.StreamHelper;
import ch.skymarshall.util.helpers.WrongCountException;

/**
 * Tricky points:
 * <ul>
 * *
 * <li>All deps must have been skipped/triggered before executing a binding,
 * because we must know the state of all dependencies to evaluate the binding
 * execution</li>
 * <li>When a binding is skipped, we must set all dependent bindings to skipped
 * </li>
 * <li>When we start evaluating a binding, we atomically check and change it's
 * state to ensure we run it only once (in case of concurrent evaluation)</li>
 * </ul>
 *
 * @author scaille
 *
 */
public class FlowToRXJavaVisitor extends AbstractJavaFlowVisitor {

	private final JavaCodeGenerator<RuntimeException> flowClass = JavaCodeGenerator.inMemory();

	private final JavaCodeGenerator<RuntimeException> flowFactories = JavaCodeGenerator.inMemory();

	private final JavaCodeGenerator<RuntimeException> flowCode = JavaCodeGenerator.inMemory();

	private final FlowGeneratorVisitor<GenContext> flowGeneratorVisitor = new FlowGeneratorVisitor<>();

	private final boolean debug;

	public FlowToRXJavaVisitor(final Flow flow, final String packageName, final Template template,
			final boolean debug) {
		super(flow, packageName, template);
		this.debug = debug;
		flowGeneratorVisitor.register(new ProcessorCallGenerator(this, flowFactories));
		flowGeneratorVisitor.register(new ConditionalFlowCtrlGenerator(this, flowFactories));
	}

	public Template process() {

		availableVars.add(new BindingImplVariable(Flow.ENTRY_POINT, flow.getEntryPointType(), "f." + Flow.ENTRY_POINT));

		super.processFlow();

		generateGlobalFlow();

		final String inputBinding = flow.getBindings().stream().filter(Binding::isEntry).map(this::varNameOf)
				.collect(StreamHelper.single()).orElseThrow(WrongCountException::new);

		final Map<String, String> templateProperties = new HashMap<>();
		templateProperties.put("package", packageName);
		templateProperties.put("flow.name", flow.getName());
		templateProperties.put("flow.input", flow.getEntryPointType());
		templateProperties.put("flow.output", "void");
		templateProperties.put("flow.executionClass", flowClass.toString());
		templateProperties.put("flow.factories", flowFactories.toString());
		templateProperties.put("flow.code", flowCode.toString());
		templateProperties.put("flow.start", inputBinding);
		templateProperties.put("imports", imports.stream().map(i -> "import " + i + ";").collect(joining("\n")));
		return template.apply(templateProperties, JavaCodeGenerator.classToSource(packageName, flow.getName()));
	}

	private void generateGlobalFlow() {
		Collections.reverse(processOrder);
		for (final BindingContext context : processOrder) {
			appendInfo(flowCode, context.binding).eol();

			final String varNameOfBinding = varNameOf(context.binding);

			final List<Binding> deps = context.getReverseDeps();
			flowCode.appendIndented("final Maybe<FlowExecution> %s = %s(execution", varNameOfBinding, varNameOfBinding);
			if (context.binding.isExit()) {
				flowCode.append(", exitModifier");
			} else {
				flowCode.append(", null");
			}
			if (!deps.isEmpty()) {
				flowCode.append(", ").append(
						deps.stream().map(d -> "() -> " + varNameOf(d) + ".subscribe()").collect(joining(", ")));
			}
			flowCode.append(")").eos();
		}
	}

	@Override
	protected void process(final BindingContext context) {

		availableVars.add(new BindingImplVariable(context.outputDataPoint, context.getProcessor().getReturnType(),
				"f." + context.outputDataPoint));
		appendInfo(flowFactories, context.binding).eol();
		generateDataState(context);
		if (!context.binding.isExit() && !definedDataPoints.contains(context.outputDataPoint)) {
			generateDataPoint(context);
		}
		visitExecution(context);
	}

	private void generateDataPoint(final BindingContext context) {
		definedDataPoints.add(context.outputDataPoint);
		generateDataSetter(context.binding.getProcessor().getReturnType(), context.outputDataPoint, true);
	}

	private void visitExecution(final BindingContext context) {
		final List<Binding> dependencies = flow.getAllDependencies(context.binding).stream()
				.sorted((b1, b2) -> b1.fromDataPoint().compareTo(b2.fromDataPoint())).collect(toList());
		GenContext genContext = new AbstractFlowGenerator.GenContext(debug, dependencies);
		flowGeneratorVisitor.generateFlow(context, genContext);

		flowFactories.appendIndented(
				"private Maybe<FlowExecution> %s(FlowExecution execution, final Function<Maybe<FlowExecution>, Maybe<FlowExecution>> callModifier, Runnable... callbacks)",
				varNameOf(context.binding)).openBlock();
		flowFactories.appendIndented("final Maybe<FlowExecution> topCall = %s(execution, callModifier, callbacks)",
				genContext.getTopCall()).eos();
		flowFactories.appendIndented("return Maybe.just(execution)").indent();

		// Call default activation when all deps are ok
		addBindingDepsCheck(context.binding, dependencies);
		
		if (debug) {
			flowFactories.eoli().append(".doOnSuccess(r -> Log.of(this).info(\"%s: Deps success\"))", context.binding)
					.eoli().append(".doOnComplete(() -> Log.of(this).info(\"%s: Deps skipping\"))", context.binding); //
		}
		flowFactories.eoli().append(".doOnSuccess(r -> topCall.subscribe())").eos().unindent();
		flowFactories.closeBlock().eol();
	}

	List<String> visitExternalAdapters(final BindingContext context, final Set<ExternalAdapter> externalAdapter) {

		final List<String> adapterNames = new ArrayList<>();

		for (final ExternalAdapter adapter : externalAdapter) {
			final String varNameOfAdapter = varNameOf(context.binding, adapter);
			if (!context.binding.isExit()) {
				generateDataSetter(adapter.getReturnType(), varNameOfAdapter, false);
				availableVars.add(new BindingImplVariable(adapter, "f." + varNameOfAdapter));
			}

			adapterNames.add("adapter_" + toVariable(adapter));
			flowFactories.appendIndentedLine("final Maybe<?> adapter_%s = Maybe.just(execution)", toVariable(adapter));
			flowFactories.indent(); //

			if (adapter.hasReturnType()) {
				flowFactories.appendIndented(".map(f -> ")
						.appendMethodCall("this", adapter.getCall(), guessParameters(context, adapter)).append(")") //
						.eoli().append(".doOnSuccess(execution::set").append(toCamelCase(varNameOfAdapter)).append(")");

			} else {
				flowFactories.appendIndented(".doOnSuccess(f -> ")
						.appendMethodCall("this", adapter.getCall(), guessParameters(context, adapter)).append(")");
			}
			flowFactories.eoli().append(".subscribeOn(Schedulers.io())");//
			flowFactories.eos().unindent().eol();
		}
		return adapterNames;
	}

	void addAdapterZip(final List<String> adapterNames) {
		if (adapterNames.isEmpty()) {
			return;
		}
		for (final String adapterName : adapterNames) {
			flowFactories.eoli().append(".zipWith(").append(adapterName).append(", (r, s) -> execution)");
		}
	}

	private void generateDataSetter(final String type, final String property, boolean withState) {
		if (withState) {
			flowClass.addVarDecl("private", "DataPointState", "state_" + property, "DataPointState.NOT_TRIGGERED");
			flowClass.addSetter("private", "DataPointState", "state_" + property);
		}
		flowClass.addVarDecl("private", type, property);
		flowClass.appendIndented(String.format("private void set%s(%s %s)", toCamelCase(property), type, property))
				.openBlock() //
				.appendIndented(String.format("this.%s = %s", property, property)).eos(); //
		if (withState) {
			flowClass.appendIndented(String.format("this.state_%s = DataPointState.TRIGGERED", property)).eos(); //
		}
		flowClass.closeBlock().eol();

	}

	private void generateDataState(final BindingContext context) {
		final String varNameOfBinding = varNameOf(context.binding);
		flowClass.addVarDecl("private", "DataPointState", bindingStateOf(context.binding),
				"DataPointState.NOT_TRIGGERED");
		flowClass.addSetter("private synchronized", "DataPointState", bindingStateOf(context.binding));

		flowClass.appendIndented("private synchronized boolean canTrigger%s()", toCamelCase(varNameOfBinding))
				.openBlock()//
				.openIf(String.format("this.%s == DataPointState.NOT_TRIGGERED", bindingStateOf(context.binding))) //
				.appendIndented("this.%s = DataPointState.TRIGGERING", bindingStateOf(context.binding)).eos() //
				.appendIndented("return true").eos().closeBlock() //
				.appendIndented("return false").eos() //
				.closeBlock();
	}

	/**
	 * Add dependencies checks.
	 * 
	 * For normal state, wait until parents are fully triggered.<br>
	 * For default dependency, wait until parents + other conditions are eith
	 * 
	 * @param binding
	 * @param dependencies
	 * @param isDefaultConditionCheck
	 * @return
	 */
	JavaCodeGenerator<RuntimeException> addBindingDepsCheck(final Binding binding, final List<Binding> dependencies) {
		if (!dependencies.isEmpty()) {
			flowFactories.eoli().append(".mapOptional(f -> (")
					.append(dependencies.stream()
							.map(d -> "(DataPointState.TRIGGERED == f." + dataPointStateOf(d)
									+ " || DataPointState.SKIPPED == f." + dataPointStateOf(d) + ")")
							.distinct().collect(joining("\n " + flowFactories.currentIndentation() + " && ")))
					.append(")?Optional.of(execution):Optional.empty())");
		}
		flowFactories.eoli().append(".mapOptional(f -> f.canTrigger%s()?Optional.of(execution):Optional.empty())",
				toCamelCase(varNameOf(binding)));

		return flowFactories;
	}

	String varNameOf(final Binding binding) {
		return "binding_" + toVariable(binding);
	}

	String bindingStateOf(final Binding binding) {
		return "state_" + varNameOf(binding);
	}

	String dataPointStateOf(final Binding binding) {
		return "state_" + binding.toDataPoint();
	}

}
