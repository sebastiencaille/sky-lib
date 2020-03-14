package ch.skymarshall.dataflowmgr.generator.writers.java;

import static java.util.stream.Collectors.joining;

import java.io.IOException;
import java.util.ArrayList;
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
		templateProperties.put("flow.input", flow.getProcessor(Flow.ENTRY_PROCESSOR).getParameter());
		templateProperties.put("flow.output", "void");
		templateProperties.put("flow.code", generator.toString());
		templateProperties.put("imports", imports.stream().map(i -> "import " + i + ";").collect(joining("\n")));
		return template.apply(templateProperties, JavaCodeGenerator.classToSource(packageName, flow.getName()));
	}

	@Override
	protected void process(final String inputParameter, final Processor processor, final String outputParameter,
			final Set<BindingRule> rules) throws IOException {

		generator.append("// ---------------- ").append(outputParameter).append(" ----------------").newLine();

		final boolean conditionalState = isConditional(inputParameter);
		final boolean hasOutput = !"void".equals(processor.getReturnType());
		final Optional<BindingRule> activator = BindingRule.get(rules, BindingRule.Type.ACTIVATION);
		final Set<String> exclusions = BindingRule.getAll(rules, BindingRule.Type.EXCLUSION)
				.map(r -> r.get(Binding.class).toProcessor()).collect(Collectors.toSet());

		final boolean conditionalExec = conditionalState || activator.isPresent() || !exclusions.isEmpty();
		if (hasOutput) {
			appendNewVariable(outputParameter, processor);
			if (conditionalExec) {
				generator.append(" = null;").newLine();
			} else {
				generator.append(" = ");
			}
		}
		if (!exclusions.isEmpty()) {
			generator.append("boolean notExcl_").append(outputParameter).append(" = ")
					.append(exclusions.stream().map(x -> x + " == null").collect(Collectors.joining(" && ")))
					.append(";").newLine();
		}
		if (conditionalExec) {
			setConditional(outputParameter);
			final List<String> conditions = new ArrayList<>();
			if (conditionalState) {
				conditions.add(inputParameter + " != null");
			}
			if (activator.isPresent()) {
				conditions.add('(' + activator.get().string() + ')');
			}
			if (!exclusions.isEmpty()) {
				conditions.add("notExcl_" + outputParameter);
			}
			generator.append("if (").append(Strings.join(conditions, " && ")).append(") ");
			generator.openBlock().appendBlank();
			if (hasOutput) {
				generator.append(outputParameter).append(" = ");
			}
		}
		generator.append(processor.getCall()).append("(").append(inputParameter).append(");").newLine();
		if (conditionalExec) {
			generator.closeBlock();
		}
		generator.newLine();
	}

	private void appendNewVariable(final String processorName, final Processor processor) throws IOException {
		String returnType = processor.getReturnType();
		if (returnType.startsWith("java.lang")) {
			returnType = returnType.substring("java.lang".length() + 1);
		}
		generator.append(returnType).append(" ").append(processorName);
	}

}
