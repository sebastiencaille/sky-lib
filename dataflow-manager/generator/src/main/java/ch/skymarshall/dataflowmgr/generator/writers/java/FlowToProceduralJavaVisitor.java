package ch.skymarshall.dataflowmgr.generator.writers.java;

import static java.util.stream.Collectors.joining;

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

public class FlowToProceduralJavaVisitor extends AbstractFlowVisitor {

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

	private final JavaCodeGenerator generator;

	private final Set<String> definedDataPoints = new HashSet<>();

	private final List<BindingImplVariable> availableVars = new ArrayList<>();

	public FlowToProceduralJavaVisitor(final Flow flow, final String packageName, final Template template) {
		super(flow);
		this.packageName = packageName;
		this.template = template;
		this.generator = new JavaCodeGenerator();
	}

	public Template process() throws IOException {

		availableVars.add(new BindingImplVariable(Flow.ENTRY_POINT, flow.getEntryPointType(), Flow.ENTRY_POINT));

		super.processFlow();

		final Map<String, String> templateProperties = new HashMap<>();
		templateProperties.put("package", packageName);
		templateProperties.put("flow.name", flow.getName());
		templateProperties.put("flow.input", flow.getEntryPointType());
		templateProperties.put("flow.output", "void");
		templateProperties.put("flow.code", generator.toString());
		templateProperties.put("imports", imports.stream().map(i -> "import " + i + ";").collect(joining("\n")));
		return template.apply(templateProperties, JavaCodeGenerator.classToSource(packageName, flow.getName()));
	}

	@Override
	protected void process(final BindingContext context, final Processor processor) throws IOException {

		generator.append("// ------------------------- ").append(context.inputDataPoint).append(" -> ")
				.append(processor.getCall()).append(" -> ").append(context.outputDataPoint)
				.append(" -------------------------").newLine();

		availableVars.add(
				new BindingImplVariable(context.outputDataPoint, processor.getReturnType(), context.outputDataPoint));

		// Init all data
		// ----------------
		final boolean isConditionalInputDataPoint = isConditional(context.inputDataPoint);

		final Set<String> exclusions = BindingRule
				.getAll(context.binding.getRules(), BindingRule.Type.EXCLUSION, Binding.class).map(Binding::toDataPoint)
				.collect(Collectors.toSet());
		final boolean isConditionalExec = isConditionalInputDataPoint || !context.activators.isEmpty()
				|| !exclusions.isEmpty();
		final boolean isExit = Flow.EXIT_PROCESSOR.equals(context.outputDataPoint);

		visitActivators(context);
		if (!isExit && !definedDataPoints.contains(context.outputDataPoint)) {
			generateDataPoint(processor, context.outputDataPoint, isConditionalExec);
		}

		// Conditional Execution
		// -------------------------
		if (!exclusions.isEmpty()) {
			generator.addVariable("boolean", executeDefaultVarNameOf(context),
					exclusions.stream().map(x -> "!" + executedVarNameOf(x)).collect(Collectors.joining(" && ")))
					.newLine();
		}
		addExecution(context, processor, exclusions, isConditionalInputDataPoint, isConditionalExec, isExit);
		generator.newLine();
	}

	private void addExecution(final BindingContext context, final Processor processor, final Set<String> exclusions,
			final boolean conditionalInputDataPoint, final boolean isConditionalExec, final boolean isExit)
			throws IOException {
		if (isConditionalExec) {
			setConditional(context.outputDataPoint);
			final List<String> conditions = new ArrayList<>();
			if (conditionalInputDataPoint) {
				conditions.add(executedVarNameOf(context.inputDataPoint));
			}
			if (!context.activators.isEmpty()) {
				conditions.add(activatedVarNameOf(context.binding));
			}
			if (!exclusions.isEmpty()) {
				conditions.add(executeDefaultVarNameOf(context));
			}
			generator.openIf(String.join(" && ", conditions));
		}

		// Execution
		visitExternalAdapters(context, context.unprocessedAdapters(context.adapters));
		if (!isExit) {
			generator.appendIndent();
			if (isConditionalExec) {
				generator.append(context.outputDataPoint).append(" = ");
			}
			appendCall(context, processor);
			if (isConditionalExec) {
				generator.appendIndented(executedVarNameOf(context.outputDataPoint)).append(" = true").eos();
			}
		}
		if (isConditionalExec) {
			generator.closeBlock();
		}
	}

	private void generateDataPoint(final Processor processor, final String outputDataPoint,
			final boolean conditionalExec) throws IOException {
		definedDataPoints.add(outputDataPoint);
		appendNewVariable(outputDataPoint, processor);
		if (conditionalExec) {
			generator.append(" = null").eos();
			generator.addVariable("boolean", executedVarNameOf(outputDataPoint), "false");
		} else {
			generator.append(" = ");
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
		generator.addVariable("boolean", activatedVarNameOf(context.binding), "true");
		for (final Condition activator : context.activators) {
			generator.openIf(activatedVarNameOf(context.binding));

			final Set<ExternalAdapter> unprocessed = context.unprocessedAdapters(listAdapters(context, activator));
			visitExternalAdapters(context, unprocessed);
			context.processedAdapters.addAll(unprocessed);

			generator.appendIndented(activatedVarNameOf(context.binding)).append(" &= ");
			appendCall(context, activator);

			generator.closeBlock();
		}
	}

	private void visitExternalAdapters(final BindingContext context, final Set<ExternalAdapter> externalAdapter)
			throws IOException {
		for (final ExternalAdapter adapter : externalAdapter) {
			final BindingImplVariable parameter = new BindingImplVariable(adapter, varNameOf(context.binding, adapter));
			appendNewVarAndCall(context, parameter.variable, adapter);
			availableVars.add(parameter);
		}
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

	private String activatedVarNameOf(final Binding binding) {
		return "activated_" + toVariable(binding);
	}

	private String executedVarNameOf(final String dataPoint) {
		return "executed_" + dataPoint;
	}

	private String executeDefaultVarNameOf(final BindingContext context) {
		return "executeDefault_" + context.outputDataPoint;
	}

	private String varNameOf(final Binding binding, final Call<?> call) {
		return call.getCall().replace('.', '_') + toVariable(binding);
	}

	private String toVariable(final WithId withId) {
		return withId.uuid().toString().replace('-', '_');
	}

	private void appendNewVariable(final String variableName, final Call<?> call) throws IOException {
		String returnType = call.getReturnType();
		if (returnType.startsWith("java.lang")) {
			returnType = returnType.substring("java.lang".length() + 1);
		}
		generator.appendIndented(returnType).append(" ").append(variableName);
	}

	private void appendCall(final BindingContext context, final Call<?> call) throws IOException {
		final String parameters = String.join(", ", guessParameters(context, call));
		generator.addMethodCall("this", call.getCall(), parameters).eos();
	}

	private void appendNewVarAndCall(final BindingContext context, final String variableName, final Call<?> call)
			throws IOException {
		if (call.hasReturnType()) {
			appendNewVariable(variableName, call);
			generator.append(" = ");
		} else {
			generator.appendIndent();
		}
		appendCall(context, call);
	}

}
