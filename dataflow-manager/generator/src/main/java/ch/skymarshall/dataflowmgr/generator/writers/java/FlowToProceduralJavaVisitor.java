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
			super();
			this.parameterName = parameterName;
			this.parameterType = parameterType;
			this.variable = variable;
		}

		public BindingImplVariable(final Call call, final String variable) {
			super();
			this.parameterName = call.getName().substring(0, call.getName().lastIndexOf('.') + 1);
			this.parameterType = call.getReturnType();
			this.variable = variable;
		}

	}

	private final Set<String> imports = new HashSet<>();

	private final String packageName;

	private final Template template;

	private final JavaCodeGenerator generator;

	private final Set<String> definedDataPoints = new HashSet<>();

	public FlowToProceduralJavaVisitor(final Flow flow, final String packageName, final Template template) {
		super(flow);
		this.packageName = packageName;
		this.template = template;
		this.generator = new JavaCodeGenerator();
	}

	public Template process() throws IOException {

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
	protected void process(final Binding binding, final String inputDataPoint, final String inputDataType,
			final Processor processor, final String outputDataPoint) throws IOException {

		generator.append("// ------------------------- ").append(processor.getCall()).append(" -> ")
				.append(outputDataPoint).append(" -------------------------").newLine();

		// Init all data
		// ----------------
		final boolean conditionalInputDataPoint = isConditional(inputDataPoint);

		final List<ExternalAdapter> adapters = binding.getAdapters();
		final Set<ExternalAdapter> adaptersNotDeclared = new HashSet<>(adapters);

		final List<Condition> activators = BindingRule.getActivators(binding.getRules()).collect(Collectors.toList());

		final Set<String> defaultBinding = BindingRule.getAll(binding.getRules(), BindingRule.Type.EXCLUSION)
				.map(r -> r.get(Binding.class).outputName()).collect(Collectors.toSet());
		final boolean conditionalExec = conditionalInputDataPoint || !activators.isEmpty() || !defaultBinding.isEmpty();

		final List<BindingImplVariable> availableVars = new ArrayList<>();
		availableVars.add(new BindingImplVariable(inputDataPoint, inputDataType, inputDataPoint));

		final boolean isExit = Flow.EXIT_PROCESSOR.equals(outputDataPoint);

		// Add activators
		// -------------------------
		if (!activators.isEmpty()) {
			generator.append("boolean ").append(activatedVariable(binding)).append(" = true;").newLine();
			visitActivators(binding, activators, availableVars, adaptersNotDeclared);
		}

		// Declare output DP
		// -------------------------
		if (!isExit && !definedDataPoints.contains(outputDataPoint)) {
			definedDataPoints.add(outputDataPoint);
			appendNewVariable(outputDataPoint, processor);
			if (conditionalExec) {
				generator.append(" = null;").newLine();
				generator.append("boolean executed_").append(outputDataPoint).append(" = false;").newLine();
			} else {
				generator.append(" = ");
			}
		}

		// Conditional Execution
		// -------------------------
		if (!defaultBinding.isEmpty()) {
			generator.append("boolean notExcl_").append(outputDataPoint).append(" = ")
					.append(defaultBinding.stream().map(x -> x + " == null").collect(Collectors.joining(" && ")))
					.append(";").newLine();
		}
		if (conditionalExec) {
			setConditional(outputDataPoint);
			final List<String> conditions = new ArrayList<>();
			if (conditionalInputDataPoint) {
				conditions.add("executed_" + inputDataPoint);
			}
			if (!activators.isEmpty()) {
				conditions.add(activatedVariable(binding));
			}
			if (!defaultBinding.isEmpty()) {
				conditions.add("notExcl_" + outputDataPoint);
			}
			generator.append("if (").append(String.join(" && ", conditions)).append(") ");
			generator.openBlock();
		}

		// Execution
		visitExternalAdapters(adaptersNotDeclared, binding, availableVars);
		if (!isExit) {
			generator.appendIndent();
			if (conditionalExec) {
				generator.append(outputDataPoint).append(" = ");
			}
			appendCall(processor, availableVars);
			if (conditionalExec) {
				generator.appendIndented("executed_").append(outputDataPoint).append(" = true;").newLine();
			}
		}
		if (conditionalExec) {
			generator.closeBlock();
		}
		generator.newLine();
	}

	private void visitActivators(final Binding binding, final List<Condition> activators,
			final List<BindingImplVariable> availableVars, final Set<ExternalAdapter> adaptersNotDeclared)
			throws IOException {
		for (final Condition activator : activators) {
			generator.append("if (").append(activatedVariable(binding)).append(")");
			generator.openBlock();

			final Set<ExternalAdapter> requiredByActivator = listAdaptersRequiredByActivator(activator,
					adaptersNotDeclared);
			visitExternalAdapters(requiredByActivator, binding, availableVars);
			adaptersNotDeclared.removeAll(requiredByActivator);

			generator.appendIndented(activatedVariable(binding)).append(" &= ");
			appendCall(activator, availableVars);

			generator.closeBlock();
		}
	}

	private void visitExternalAdapters(final Set<ExternalAdapter> requiredByActivator, final Binding binding,
			final List<BindingImplVariable> availableVars) throws IOException {
		for (final ExternalAdapter adapter : requiredByActivator) {
			final BindingImplVariable parameter = new BindingImplVariable(adapter, variableNameOf(binding, adapter));
			appendNewVarAndCall(parameter.variable, adapter, availableVars);
			availableVars.add(parameter);
		}
	}

	private List<String> guessParameters(final Call call, final List<BindingImplVariable> availableVars) {
		return call.getParameters().entrySet().stream()
				.map(kv -> guessParameter(kv.getKey(), kv.getValue(), availableVars)).collect(Collectors.toList());
	}

	private String guessParameter(final String paramName, final String paramType,
			final List<BindingImplVariable> availableVars) {
		List<BindingImplVariable> matches = availableVars.stream().filter(a -> a.parameterName.equals(paramName))
				.collect(Collectors.toList());
		if (matches.size() > 1) {
			throw new IllegalArgumentException("Too many possible parameters found for " + paramName);
		} else if (matches.size() == 1) {
			return matches.get(0).variable;
		}
		matches = availableVars.stream().filter(a -> a.parameterType.equals(paramType)).collect(Collectors.toList());
		if (matches.size() > 1) {
			throw new IllegalArgumentException("Too many possible parameters found for " + paramType);
		} else if (matches.size() == 1) {
			return matches.get(0).variable;
		}
		throw new IllegalStateException("No parameter found for " + paramName + "/" + paramType);
	}

	private String activatedVariable(final Binding binding) {
		return "activated_" + toVariable(binding);
	}

	private String toVariable(final WithId withId) {
		return withId.uuid().toString().replace('-', '_');
	}

	private void appendNewVariable(final String variableName, final Call call) throws IOException {
		String returnType = call.getReturnType();
		if (returnType.startsWith("java.lang")) {
			returnType = returnType.substring("java.lang".length() + 1);
		}
		generator.appendIndented(returnType).append(" ").append(variableName);
	}

	private String variableNameOf(final Binding binding, final Call call) {
		return call.getCall().replace('.', '_') + toVariable(binding);
	}

	private void appendCall(final Call call, final List<BindingImplVariable> availableVars) throws IOException {
		final String parameters = String.join(", ", guessParameters(call, availableVars));
		generator.append(call.getCall()).append("(").append(parameters).append(");").newLine();
	}

	private void appendNewVarAndCall(final String variableName, final Call call,
			final List<BindingImplVariable> availableVars) throws IOException {
		if (call.hasReturnType()) {
			appendNewVariable(variableName, call);
			generator.append(" = ");
		} else {
			generator.appendIndent();
		}
		appendCall(call, availableVars);
	}

}
