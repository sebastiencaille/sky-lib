package ch.skymarshall.dataflowmgr.generator.writers.javaproc;

import static java.util.stream.Collectors.joining;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import ch.skymarshall.dataflowmgr.generator.writers.AbstractJavaFlowVisitor;
import ch.skymarshall.dataflowmgr.generator.writers.FlowGeneratorVisitor;
import ch.skymarshall.dataflowmgr.model.Call;
import ch.skymarshall.dataflowmgr.model.ExternalAdapter;
import ch.skymarshall.dataflowmgr.model.Flow;
import ch.skymarshall.util.generators.JavaCodeGenerator;
import ch.skymarshall.util.generators.Template;

public class FlowToProceduralJavaVisitor extends AbstractJavaFlowVisitor {

	private final JavaCodeGenerator<RuntimeException> generator = JavaCodeGenerator.inMemory();

	private final FlowGeneratorVisitor<Void> flowGeneratorVisitor = new FlowGeneratorVisitor<>();

	public FlowToProceduralJavaVisitor(final Flow flow, final String packageName, final Template template) {
		super(flow, packageName, template);
		flowGeneratorVisitor.register(new ConditionalFlowCtrlGenerator(this, generator));
		flowGeneratorVisitor.register(new ProcessorCallGenerator(this, generator));
	}

	public Template process() {

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
	protected void process(final BindingContext context) {
		appendInfo(generator, context.binding).eol();

		availableVars.add(new BindingImplVariable(context.outputDataPoint, context.getProcessor().getReturnType(),
				context.outputDataPoint));

		flowGeneratorVisitor.generateFlow(context, null);
		generator.eol();
	}

	void visitExternalAdapters(final BindingContext context, final Set<ExternalAdapter> externalAdapter) {
		for (final ExternalAdapter adapter : externalAdapter) {
			final BindingImplVariable parameter = new BindingImplVariable(adapter, varNameOf(context.binding, adapter));
			appendNewVarAndCall(context, parameter.codeVariable, adapter);
			availableVars.add(parameter);
		}
	}

	public void appendNewVariable(final String variableName, final Call<?> call) {
		String returnType = call.getReturnType();
		if (returnType.startsWith("java.lang")) {
			returnType = returnType.substring("java.lang".length() + 1);
		}
		generator.appendIndented(returnType).append(" ").append(variableName);
	}

	void appendCall(final BindingContext context, final Call<?> call) {
		generator.appendMethodCall("this", call.getCall(), guessParameters(context, call)).eos();
	}

	private void appendNewVarAndCall(final BindingContext context, final String variableName, final Call<?> call) {
		if (call.hasReturnType()) {
			appendNewVariable(variableName, call);
			generator.append(" = ");
		} else {
			generator.appendIndent();
		}
		appendCall(context, call);
	}

}
