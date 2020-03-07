package ch.skymarshall.dataflowmgr.generator.writers.java;

import static java.util.stream.Collectors.joining;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import ch.skymarshall.dataflowmgr.generator.AbstractFlowVisitor;
import ch.skymarshall.dataflowmgr.model.Flow;
import ch.skymarshall.dataflowmgr.model.Processor;
import ch.skymarshall.util.generators.JavaCodeGenerator;
import ch.skymarshall.util.generators.Template;

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
	protected void process(final String processorName, final Processor processor, final String inputName)
			throws IOException {

		if (!"void".equals(processor.getReturnType())) {
			imports.add(processor.getReturnType());
			generator.append(processor.getReturnType()).append(" ").append(processorName).append(" = ");
		}
		generator.append(processor.getCall()).append("(").append(inputName).append(");").newLine();
	}

}
