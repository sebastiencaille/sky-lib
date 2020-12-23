package ch.skymarshall.dataflowmgr.generator.writers.java;

import static ch.skymarshall.util.text.TextFormatter.toCamelCase;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import ch.skymarshall.dataflowmgr.model.Binding;
import ch.skymarshall.dataflowmgr.model.BindingRule;
import ch.skymarshall.dataflowmgr.model.Condition;
import ch.skymarshall.dataflowmgr.model.ConditionalBindingGroup;
import ch.skymarshall.dataflowmgr.model.ExternalAdapter;
import ch.skymarshall.dataflowmgr.model.Flow;
import ch.skymarshall.dataflowmgr.model.Processor;
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
public class FlowToRXJavaVisitor extends AbstractJavaVisitor {

	private final JavaCodeGenerator<RuntimeException> flowClass = JavaCodeGenerator.inMemory();

	private final JavaCodeGenerator<RuntimeException> flowFactories = JavaCodeGenerator.inMemory();

	private final JavaCodeGenerator<RuntimeException> flowCode = JavaCodeGenerator.inMemory();

	private final boolean debug;

	public FlowToRXJavaVisitor(final Flow flow, final String packageName, final Template template,
			final boolean debug) {
		super(flow, packageName, template);
		this.debug = debug;
	}

	public Template process() {

		availableVars.add(new BindingImplVariable(Flow.ENTRY_POINT, flow.getEntryPointType(), "f." + Flow.ENTRY_POINT));

		super.processFlow();

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

	@Override
	protected void process(final BindingContext context, final Processor processor) {

		availableVars.add(new BindingImplVariable(context.outputDataPoint, processor.getReturnType(),
				"f." + context.outputDataPoint));

		appendInfo(flowFactories, context.binding).eol();

		// Init all data
		// ----------------

		final Set<Binding> exclusions = BindingRule
				.getAll(context.binding.getRules(), BindingRule.Type.EXCLUSION, Binding.class).collect(toSet());
		final boolean isConditionalExec = !context.activators.isEmpty();
		final boolean isExit = context.binding.isExit();

		final String varNameOfBinding = varNameOf(context.binding);
		flowClass.addVarDecl("private", "DataPointState", bindingStateOf(context.binding),
				"DataPointState.NOT_TRIGGERED");
		flowClass.addSetter("private", "DataPointState", bindingStateOf(context.binding));

		flowClass.appendIndented("private synchronized boolean canTrigger%s()", toCamelCase(varNameOfBinding))
				.openBlock()//
				.openIf(String.format("this.%s == DataPointState.NOT_TRIGGERED", bindingStateOf(context.binding))) //
				.appendIndented("this.%s = DataPointState.TRIGGERING", bindingStateOf(context.binding)).eos() //
				.appendIndented("return true").eos().closeBlock() //
				.appendIndented("return false").eos() //
				.closeBlock();

		final List<Binding> dependencies = flow.getAllDependencies(context.binding).stream()
				.sorted((b1, b2) -> b1.fromDataPoint().compareTo(b2.fromDataPoint())).collect(toList());

		if (!isExit && !definedDataPoints.contains(context.outputDataPoint)) {
			generateDataPoint(context);
		}

		flowFactories.appendIndented(
				"private Maybe<FlowExecution> %s(FlowExecution execution, final Function<Maybe<FlowExecution>, Maybe<FlowExecution>> callModifier, Runnable... callbacks)",
				varNameOfBinding).openBlock();

		visitExecution(context, processor, dependencies, exclusions, isConditionalExec);
		visitActivators(context, dependencies);

		if (debug) {
			flowFactories.eoli()
					.append("first = first.doOnSuccess(r -> System.out.println(\"%s: Deps success\"))", context.binding)
					.indent() //
					.eoli().append(".doOnComplete(() -> System.out.println(\"%s: Deps skipping\"))", context.binding)
					.eos().unindent(); //
		}

		flowFactories.appendIndented("return first").eos();
		flowFactories.closeBlock().eol();

	}

	private void generateDataPoint(final BindingContext context) {
		definedDataPoints.add(context.outputDataPoint);
		addDataSetter(context.binding.getProcessor().getReturnType(), context.outputDataPoint, true);
	}

	private void visitExecution(final BindingContext context, final Processor processor,
			final List<Binding> bindingDeps, final Set<Binding> exclusions, final boolean isConditionalExec) {

		final List<String> adapterNames = visitExternalAdapters(context,
				context.unprocessedAdapters(context.bindingAdapters));

		// Call service if not excluded
		flowFactories.appendIndented("Maybe<FlowExecution> callService = Maybe.just(execution)").indent() //
				.eoli().append(".doOnSuccess(e -> e.setState%s(DataPointState.TRIGGERED))",
						toCamelCase(varNameOf(context.binding)));
		if (!exclusions.isEmpty()) {
			flowFactories
					.eoli().append(".mapOptional(f -> ").append(exclusions.stream()
							.map(x -> "DataPointState.SKIPPED == f." + dataPointStateOf(x)).collect(joining(" && ")))
					.append("?Optional.of(f):Optional.empty())");
		}
		flowFactories.eoli().append(".doOnComplete(() -> execution.set")
				.append(toCamelCase(bindingStateOf(context.binding))).append("(DataPointState.SKIPPED))");
		if (debug) {
			flowFactories.eoli().append(".doOnSuccess(r -> System.out.println(\"%s: Call success\"))", context.binding)
					.eoli().append(".doOnComplete(() -> System.out.println(\"%s: Call skipped\"))", context.binding); //
		}

		if (!context.binding.isExit()) {
			flowFactories.eoli().append(".doOnSuccess(f -> f.set%s(", toCamelCase(context.outputDataPoint)) //
					.appendMethodCall("this", processor.getCall(), guessParameters(context, processor)).append("))");
		}
		flowFactories.eoli().append(".doOnTerminate(() -> Arrays.stream(callbacks).forEach(Runnable::run))")//
				.eoli().append(".subscribeOn(Schedulers.computation())") //
				.eos().unindent();

		// Refine call
		flowFactories.openIf("callModifier != null") //
				.appendIndented("callService = callModifier.apply(callService)").eos()//
				.closeBlock();
		flowFactories.appendIndented("callService.subscribeOn(Schedulers.computation())").eos();

		// Call adapters
		if (!adapterNames.isEmpty()) {
			flowFactories.eoli().append("final Maybe<FlowExecution> callServiceConst = callService").eos();
			flowFactories
					.appendIndented("final Maybe<FlowExecution> callAdaptersAndServiceConst = Maybe.just(execution)")
					.indent(); //
			addAdapterZip(adapterNames);
			flowFactories.eoli().append(".flatMap(r -> callServiceConst)").eos().unindent();
		} else {
			flowFactories.appendIndented("final Maybe<FlowExecution> callAdaptersAndServiceConst = callService").eos()
					.eol();
		}

		if (!isConditionalExec) {
			// Subscribe because we want to Complete only after all deps are executed
			flowFactories.appendIndented("Maybe<FlowExecution> first = Maybe.just(execution)").indent(); //
			addBindingDepsCheck(context.binding, bindingDeps) //
					.eoli().append(".doOnSuccess(r -> callAdaptersAndServiceConst.subscribe())").eos().unindent();
		}

	}

	private String bindingStateOf(final Binding binding) {
		return "state_" + varNameOf(binding);
	}

	private String dataPointStateOf(final Binding binding) {
		return "state_" + binding.toDataPoint();
	}

	/**
	 * Generates the code calling a list of activators
	 *
	 * @param context
	 * @param availableVars @
	 */
	private void visitActivators(final BindingContext context, final List<Binding> bindingDeps) {
		if (context.activators.isEmpty()) {
			return;
		}
		final List<String> activatorNames = new ArrayList<>();

		// Activators
		for (final Condition activator : context.activators) {

			final Set<ExternalAdapter> unprocessed = context.unprocessedAdapters(listAdapters(context, activator));
			final List<String> adapterNames = visitExternalAdapters(context, unprocessed);
			context.processedAdapters.addAll(unprocessed);

			final List<String> activatorParameters = guessParameters(context, activator);

			activatorNames.add("activator_" + toVariable(activator));

			flowFactories
					.appendIndented("final Maybe<Boolean> activator_%s = Maybe.just(execution)", toVariable(activator))
					.indent();
			addAdapterZip(adapterNames);
			flowFactories.eoli() //
					.append(".map(f -> ").appendMethodCall("this", activator.getCall(), activatorParameters).append(")") //
					.eoli().append(".subscribeOn(Schedulers.computation())").eos().unindent().eol();
		}

		// Activation check
		flowFactories.appendIndented("final Maybe<FlowExecution> activationCheck = Maybe.just(true)").indent();
		for (final String activatorName : activatorNames) {
			flowFactories.eoli().append(".zipWith(").append(activatorName)
					.append(", (u, r) -> u.booleanValue() && r.booleanValue())");
		}
		flowFactories.eoli().append(".mapOptional(b -> b ? Optional.of(execution) : Optional.empty())") //
				.eoli().append(".flatMap(e -> callAdaptersAndServiceConst)") //
				.eoli()
				.append(".doOnComplete(() -> { execution.setState%s(DataPointState.TRIGGERED); execution.setState%s(DataPointState.SKIPPED); })",
						toCamelCase(varNameOf(context.binding)), toCamelCase(context.binding.toDataPoint())) //
				.eoli().append(".doOnTerminate(() -> Arrays.stream(callbacks).forEach(Runnable::run))") //
				.eos().unindent().eol();

		// Call default activation when all deps are ok
		flowFactories.appendIndented("Maybe<FlowExecution> first = Maybe.just(execution)").indent();
		addBindingDepsCheck(context.binding, bindingDeps). //
				eoli().append(".doOnSuccess(r -> activationCheck.subscribe())").eos().unindent();
	}

	private List<String> visitExternalAdapters(final BindingContext context,
			final Set<ExternalAdapter> externalAdapter) {

		final List<String> adapterNames = new ArrayList<>();

		for (final ExternalAdapter adapter : externalAdapter) {
			final String varNameOfAdapter = varNameOf(context.binding, adapter);
			if (!context.binding.isExit()) {
				addDataSetter(adapter.getReturnType(), varNameOfAdapter, false);
				final BindingImplVariable parameter = new BindingImplVariable(adapter, "f." + varNameOfAdapter);
				availableVars.add(parameter);
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

	private void addDataSetter(final String type, final String property, boolean withState) {
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
	private JavaCodeGenerator<RuntimeException> addBindingDepsCheck(final Binding binding,
			final List<Binding> dependencies) {
		if (!dependencies.isEmpty()) {
			final Optional<ConditionalBindingGroup> condition = BindingRule.getCondition(binding.getRules());
			flowFactories.eoli().append(".mapOptional(f -> (")
					.append(dependencies.stream().map(d -> "(DataPointState.TRIGGERED == f." + dataPointStateOf(d)
							+ (isExclusion(condition, d) ? " || DataPointState.SKIPPED == f." + dataPointStateOf(d)
									: "")
							+ ")").distinct().collect(joining("\n " + flowFactories.currentIndentation() + " && ")))
					.append(")?Optional.of(execution):Optional.empty())");
		}
		flowFactories.eoli().append(".mapOptional(f -> f.canTrigger%s()?Optional.of(execution):Optional.empty())",
				toCamelCase(varNameOf(binding)));

		return flowFactories;
	}

	private boolean isExclusion(Optional<ConditionalBindingGroup> condition, Binding dependency) {
		if (!condition.isPresent()) {
			return false;
		}
		Optional<ConditionalBindingGroup> dependencyCondition = BindingRule.getCondition(dependency.getRules());
		return condition.get().getName()
				.equals(dependencyCondition.map(ConditionalBindingGroup::getName).orElse("---"));
	}

	private void addAdapterZip(final List<String> adapterNames) {
		if (adapterNames.isEmpty()) {
			return;
		}
		for (final String adapterName : adapterNames) {
			flowFactories.eoli().append(".zipWith(").append(adapterName).append(", (r, s) -> execution)");
		}
	}

	private String varNameOf(final Binding binding) {
		return "binding_" + toVariable(binding);
	}
}
