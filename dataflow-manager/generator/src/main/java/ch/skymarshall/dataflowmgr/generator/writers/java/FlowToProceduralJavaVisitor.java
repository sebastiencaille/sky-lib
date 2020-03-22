package ch.skymarshall.dataflowmgr.generator.writers.java;

import static java.util.stream.Collectors.joining;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import ch.skymarshall.dataflowmgr.generator.AbstractFlowVisitor;
import ch.skymarshall.dataflowmgr.model.Binding;
import ch.skymarshall.dataflowmgr.model.BindingRule;
import ch.skymarshall.dataflowmgr.model.Call;
import ch.skymarshall.dataflowmgr.model.ExternalAdapter;
import ch.skymarshall.dataflowmgr.model.Flow;
import ch.skymarshall.dataflowmgr.model.Processor;
import ch.skymarshall.util.generators.JavaCodeGenerator;
import ch.skymarshall.util.generators.Template;
import joptsimple.internal.Strings;

public class FlowToProceduralJavaVisitor extends AbstractFlowVisitor {

	private final Set<String> imports = new HashSet<>();

	private final String packageName;

	private final Template template;

	private final JavaCodeGenerator generator;

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
		templateProperties.put("flow.input", flow.getProcessor(Flow.ENTRY_PROCESSOR).getParameters().get(0));
		templateProperties.put("flow.output", "void");
		templateProperties.put("flow.code", generator.toString());
		templateProperties.put("imports", imports.stream().map(i -> "import " + i + ";").collect(joining("\n")));
		return template.apply(templateProperties, JavaCodeGenerator.classToSource(packageName, flow.getName()));
	}

	@Override
	protected void process(final Binding binding, final String inputParameter, final Processor processor,
			final String processorName) throws IOException {

		generator.append("// ---------------- ").append(processorName).append(" ----------------").newLine();

		final boolean conditionalState = isConditional(inputParameter);
		final boolean hasOutput = !"void".equals(processor.getReturnType());
		final Optional<String> activator = BindingRule.getActivator(binding.getRules());
		final Set<String> exclusions = BindingRule.getAll(binding.getRules(), BindingRule.Type.EXCLUSION)
				.map(r -> r.get(Binding.class).toProcessor()).collect(Collectors.toSet());
		final boolean conditionalExec = conditionalState || activator.isPresent() || !exclusions.isEmpty();

		// Output declaration
		if (hasOutput) {
			appendNewVariable(processorName, processor);
			if (conditionalExec) {
				generator.append(" = null;").newLine();
			} else {
				generator.append(" = ");
			}
		}

		// Conditional Execution
		if (!exclusions.isEmpty()) {
			generator.append("boolean notExcl_").append(processorName).append(" = ")
					.append(exclusions.stream().map(x -> x + " == null").collect(Collectors.joining(" && ")))
					.append(";").newLine();
		}
		if (conditionalExec) {
			setConditional(processorName);
			final List<String> conditions = new ArrayList<>();
			if (conditionalState) {
				conditions.add(inputParameter + " != null");
			}
			if (activator.isPresent()) {
				conditions.add(activator.get() + '(' + inputParameter + ')');
			}
			if (!exclusions.isEmpty()) {
				conditions.add("notExcl_" + processorName);
			}
			generator.append("if (").append(Strings.join(conditions, " && ")).append(") ");
			generator.openBlock();
		}

		// Adapters
		final List<String> parameterNames = new ArrayList<>();
		parameterNames.add(inputParameter);
		for (final String adapterName : binding.getAdapters()) {
			generator.appendIndent();
			visit(adapterName, flow.getAdapter(adapterName), inputParameter).ifPresent(parameterNames::add);
		}

		// Execution
		if (!Flow.EXIT_PROCESSOR.equals(processorName)) {
			generator.appendIndent();
			if (hasOutput && conditionalExec) {
				generator.append(processorName).append(" = ");
			}
			appendCall(processor, parameterNames);
		}
		if (conditionalExec) {
			generator.closeBlock();
		}
		generator.newLine();
	}

	private Optional<String> visit(final String adapterName, final ExternalAdapter adapter, final String parameterName)
			throws IOException {
		if (adapter.hasReturnType()) {
			appendNewVariable(adapterName, adapter);
			generator.append(" = ");
		}
		appendCall(adapter, Collections.singletonList(parameterName));
		if (adapter.hasReturnType()) {
			return Optional.of(adapterName);
		}
		return Optional.empty();
	}

	private void appendNewVariable(final String variableName, final Call call) throws IOException {
		String returnType = call.getReturnType();
		if (returnType.startsWith("java.lang")) {
			returnType = returnType.substring("java.lang".length() + 1);
		}
		generator.append(returnType).append(" ").append(variableName);
	}

	private void appendCall(final Call call, final List<String> parameterNames) throws IOException {
		generator.append(call.getCall()).append("(").append(String.join(", ", parameterNames)).append(");").newLine();
	}

}
