package ch.skymarshall.dataflowmgr.generator.writers.java;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toSet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import ch.skymarshall.dataflowmgr.generator.AbstractFlowVisitor;
import ch.skymarshall.dataflowmgr.model.Binding;
import ch.skymarshall.dataflowmgr.model.BindingRule;
import ch.skymarshall.dataflowmgr.model.Call;
import ch.skymarshall.dataflowmgr.model.Condition;
import ch.skymarshall.dataflowmgr.model.ExternalAdapter;
import ch.skymarshall.dataflowmgr.model.Flow;
import ch.skymarshall.dataflowmgr.model.Processor;
import ch.skymarshall.dataflowmgr.model.WithId;
import ch.skymarshall.util.generators.JavaCodeGenerator;
import ch.skymarshall.util.generators.Template;

public class FlowToRXJavaVisitor extends AbstractFlowVisitor {

	private static class BindingImplVariable {
		private final String parameterName;
		private final String parameterType;
		private final String variable;

		public BindingImplVariable(final String parameterName, final String parameterType, final String variable) {
			this.parameterName = parameterName;
			this.parameterType = parameterType;
			this.variable = variable;
		}

		public BindingImplVariable(final Call<?> call, final String variable) {
			this.parameterName = call.getName().substring(call.getName().lastIndexOf('.') + 1);
			this.parameterType = call.getReturnType();
			this.variable = variable;
		}

		@Override
		public String toString() {
			return parameterName + ": " + parameterType;
		}

	}

	private final Set<String> imports = new HashSet<>();

	private final String packageName;

	private final Template template;

	private final JavaCodeGenerator flowClass = new JavaCodeGenerator();

	private final JavaCodeGenerator flowFactories = new JavaCodeGenerator();

	private final JavaCodeGenerator flowCode = new JavaCodeGenerator();

	private final Set<String> definedDataPoints = new HashSet<>();

	private final List<BindingImplVariable> availableVars = new ArrayList<>();

	public FlowToRXJavaVisitor(final Flow flow, final String packageName, final Template template) {
		super(flow);
		this.packageName = packageName;
		this.template = template;
	}

	public Template process() throws IOException {

		availableVars.add(new BindingImplVariable(Flow.ENTRY_POINT, flow.getEntryPointType(), "f." + Flow.ENTRY_POINT));

		super.processFlow();

		final Map<String, String> templateProperties = new HashMap<>();
		templateProperties.put("package", packageName);
		templateProperties.put("flow.name", flow.getName());
		templateProperties.put("flow.input", flow.getEntryPointType());
		templateProperties.put("flow.output", "void");
		templateProperties.put("flow.executionClass", flowClass.toString());
		templateProperties.put("flow.factories", flowFactories.toString());
		templateProperties.put("flow.code", flowCode.toString());
		templateProperties.put("imports", imports.stream().map(i -> "import " + i + ";").collect(joining("\n")));
		return template.apply(templateProperties, JavaCodeGenerator.classToSource(packageName, flow.getName()));
	}

	@Override
	protected void process(final BindingContext context, final Processor processor) throws IOException {

		availableVars.add(new BindingImplVariable(context.outputDataPoint, processor.getReturnType(),
				"f." + context.outputDataPoint));

		flowFactories.append("// ------------------------- ") //
				.append(context.inputDataPoint).append(" -> ") //
				.append(processor.getCall()).append(" -> ")//
				.append(context.outputDataPoint) //
				.append(" -------------------------").eol();

		// Init all data
		// ----------------
		final boolean isConditionalInputDataPoint = isConditional(context.inputDataPoint);

		final Set<String> exclusions = BindingRule
				.getAll(context.binding.getRules(), BindingRule.Type.EXCLUSION, Binding.class).map(Binding::toDataPoint)
				.collect(toSet());
		final boolean isConditionalExec = !context.activators.isEmpty();
		final boolean isExit = Flow.EXIT_PROCESSOR.equals(context.outputDataPoint);

		flowFactories.appendIndented("private Maybe<FlowExecution> ").append("binding_")
				.append(toVariable(context.binding)).append("(FlowExecution execution)").openBlock();

		visitExecution(context, processor, exclusions, isConditionalInputDataPoint, isConditionalExec, isExit);

		visitActivators(context);
		if (!isExit && !definedDataPoints.contains(context.outputDataPoint)) {
			generateDataPoint(processor, context, isConditionalExec);
		}

		// Conditional Execution
		// -------------------------

		final String bindingCall = "binding_" + toVariable(context.binding) + "(execution)";
		flowCode.eoli().append(".flatMap(s -> ").append(bindingCall).append(", e -> null, () -> ").append(bindingCall)
				.append(")");
		flowFactories.closeBlock().eol();

	}

	private void generateDataPoint(final Processor processor, final BindingContext context,
			final boolean conditionalExec) throws IOException {
		definedDataPoints.add(context.outputDataPoint);
		flowClass.addVarDecl("private", processor.getReturnType(), context.outputDataPoint);
		flowClass.addSetter("private", processor.getReturnType(), context.outputDataPoint).eol();
		if (conditionalExec) {
			flowClass.addVarDecl("private", "boolean", availableVarNameOf(context.outputDataPoint));
			flowClass.addSetter("private", "boolean", availableVarNameOf(context.outputDataPoint)).eol();
		}
	}

	private void visitExecution(final BindingContext context, final Processor processor, final Set<String> exclusions,
			final boolean conditionalInputDataPoint, final boolean isConditionalExec, final boolean isExit)
			throws IOException {

		// Execution
		final List<String> adapterNames = visitExternalAdapters(context, context.unprocessedAdapters(context.adapters));

		flowFactories.appendIndented("final Maybe<FlowExecution> call = ");
		if (adapterNames.isEmpty()) {
			flowFactories.append("Single.just(execution)");
		} else {
			flowFactories.append("Single.zipArray(a -> execution, ").append(String.join(",\n", adapterNames))
					.append(")"); //
		}
		if (isExit) {
			flowFactories.append(".toMaybe()").eos().appendIndented("return call").eos();
			return;
		}

		flowFactories.indent().eoli().append(".subscribeOn(Schedulers.computation())").eoli(); //
		if (!exclusions.isEmpty()) {
			flowFactories.append(".mapOptional(f -> ")
					.append(exclusions.stream().map(x -> "!f." + availableVarNameOf(x)).collect(joining(" &&\n")))
					.append("?Optional.of(f):Optional.empty())").eoli();
		}
		flowFactories.append(".doOnSuccess(f -> f.set").appendCamelCase(context.outputDataPoint).append("(")
				.appendMethodCall("this", processor.getCall(), guessParameters(context, processor)).append("))");
		if (isConditionalExec) {
			flowFactories.eoli().append(".doOnSuccess(f -> f.set")
					.appendCamelCase(availableVarNameOf(context.outputDataPoint)).append("(true))");
		}
		if (exclusions.isEmpty()) {
			flowFactories.eoli().appendIndented(".toMaybe()");
		}
		flowFactories.eos().unindent();
		if (!isConditionalExec) {
			flowFactories.appendIndented("return call").eos();
		}

	}

	/**
	 * Generates the code calling a list of activators
	 *
	 * @param context
	 * @param availableVars
	 * @throws IOException
	 */
	private void visitActivators(final BindingContext context) throws IOException {
		if (context.activators.isEmpty()) {
			return;
		}
		final List<String> activatorNames = new ArrayList<>();

		for (final Condition activator : context.activators) {

			final Set<ExternalAdapter> unprocessed = context.unprocessedAdapters(listAdapters(context, activator));
			final List<String> adapterNames = visitExternalAdapters(context, unprocessed);
			context.processedAdapters.addAll(unprocessed);

			final List<String> activatorParameters = guessParameters(context, activator);

			activatorNames.add("activator_" + toVariable(activator));
			flowFactories.appendIndented("final Single<Boolean> activator_").append(toVariable(activator))
					.append(" = ");
			if (!adapterNames.isEmpty()) {
				flowFactories.append("Single.zipArray(a -> execution, ").append(String.join(",\n", adapterNames))
						.append(")");
			} else {
				flowFactories.append("Single.just(execution)");
			}
			flowFactories.indent().eoli() //
					.append(".subscribeOn(Schedulers.computation())").eoli() //
					.append(".map(f -> ").appendMethodCall("this", activator.getCall(), activatorParameters).append(")")
					.eos().unindent().eol();
		}
		flowFactories.appendIndented("return Single.zipArray(a -> Stream.of(a).map(b->(Boolean)b).allMatch(b->b), ")
				.append(String.join(",\n", activatorNames)).append(")").indent().eoli() //
				.append(".toMaybe()").eoli() //
				.append(".mapOptional(b-> b ? Optional.of(execution) : Optional.empty())").eoli() //
				.append(".flatMap(e -> call)").eos(). //
				unindent().eol();
	}

	private List<String> visitExternalAdapters(final BindingContext context, final Set<ExternalAdapter> externalAdapter)
			throws IOException {

		final List<String> adapterNames = new ArrayList<>();

		for (final ExternalAdapter adapter : externalAdapter) {
			final String varNameOfAdapter = varNameOf(context.binding, adapter);
			if (adapter.hasReturnType()) {
				flowClass.addVarDecl("private", adapter.getReturnType(), varNameOfAdapter);
				flowClass.addSetter("private", adapter.getReturnType(), varNameOfAdapter);
				final BindingImplVariable parameter = new BindingImplVariable(adapter, "f." + varNameOfAdapter);
				availableVars.add(parameter);
			}

			adapterNames.add("adapter_" + toVariable(adapter));
			flowFactories.appendIndented("final Single<?> adapter_").append(toVariable(adapter))
					.append(" = Single.just(execution) //").indent().eoli() //
					.append(".subscribeOn(Schedulers.io())").eol(); //
			if (adapter.hasReturnType()) {
				flowFactories.appendIndented(".map(f -> ")
						.appendMethodCall("this", adapter.getCall(), guessParameters(context, adapter)).append(")")
						.eoli(); //
				flowFactories.append(".subscribeOn(Schedulers.computation())").eoli() //
						.append(".doOnSuccess(execution::set").appendCamelCase(varNameOfAdapter).append(")");

			} else {
				flowFactories.appendIndented(".doOnSuccess(f -> ")
						.appendMethodCall("this", adapter.getCall(), guessParameters(context, adapter)).append(")");
			}
			flowFactories.eos().unindent().eol();
		}
		return adapterNames;
	}

	private List<String> guessParameters(final BindingContext context, final Call<?> call) {
		return call.getParameters().entrySet().stream().map(kv -> guessParameter(context, kv.getKey(), kv.getValue()))
				.collect(Collectors.toList());
	}

	private String guessParameter(final BindingContext context, final String paramName, final String paramType) {
		if (paramType.equals(context.inputDataType)) {
			return availableVars.stream().filter(a -> a.parameterName.equals(context.inputDataPoint)).findFirst()
					.map(v -> v.variable).get();
		}
		List<BindingImplVariable> matches = availableVars.stream().filter(a -> a.parameterName.equals(paramName))
				.collect(Collectors.toList());
		if (matches.size() > 1) {
			throw new IllegalArgumentException("Too many possible parameters found for " + paramName + ": " + matches);
		} else if (matches.size() == 1) {
			return matches.get(0).variable;
		}
		matches = availableVars.stream().filter(a -> a.parameterType.equals(paramType)).collect(Collectors.toList());
		if (matches.size() > 1) {
			throw new IllegalArgumentException("Too many possible parameters found for " + paramType + ": " + matches);
		} else if (matches.size() == 1) {
			return matches.get(0).variable;
		}
		throw new IllegalStateException("No parameter found for " + paramName + "/" + paramType);
	}

	/**
	 * Specifies if a binding a data point is available (executed by any possible
	 * binding)
	 *
	 * @param dataPoint
	 * @return
	 */
	private String availableVarNameOf(final String dataPoint) {
		return dataPoint + "_available";
	}

	private String varNameOf(final Binding binding, final Call<?> call) {
		return call.getCall().replace('.', '_') + toVariable(binding);
	}

	private String toVariable(final WithId withId) {
		return withId.uuid().toString().replace('-', '_');
	}

}
