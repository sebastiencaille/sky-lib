package ch.scaille.dataflowmgr.generator.writers.javarx;

import static java.util.stream.Collectors.joining;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import ch.scaille.dataflowmgr.generator.writers.AbstractJavaFlowVisitor;
import ch.scaille.dataflowmgr.generator.writers.FlowGeneratorVisitor;
import ch.scaille.dataflowmgr.generator.writers.javarx.AbstractFlowGenerator.GenContext;
import ch.scaille.dataflowmgr.model.Call;
import ch.scaille.dataflowmgr.model.Processor;
import ch.scaille.dataflowmgr.model.Flow;
import ch.scaille.generators.util.JavaCodeGenerator;
import ch.scaille.generators.util.Template;
import ch.scaille.util.helpers.StreamExt;
import ch.scaille.util.helpers.WrongCountException;

/**
 * Tricky points:
 * <ul>
 * *
 * <li>All deps must have been triggered before executing a call,
 * because we must know the state of all dependencies to evaluate the call
 * execution</li>
 * <li>When we start evaluating a call, we atomically check and change it's
 * state to ensure we run it only once (in case of concurrent evaluation)</li>
 * </ul>
 *
 * @author scaille
 *
 */
public class FlowToRXJavaVisitor extends AbstractJavaFlowVisitor {

	private final JavaCodeGenerator<RuntimeException> flowExecutionAttributes = JavaCodeGenerator.inMemory();

	private final JavaCodeGenerator<RuntimeException> flowExecutionBuilder = JavaCodeGenerator.inMemory();

	private final JavaCodeGenerator<RuntimeException> flowExecutionDependencies = JavaCodeGenerator.inMemory();

	private final JavaCodeGenerator<RuntimeException> flowCode = JavaCodeGenerator.inMemory();

	private final FlowGeneratorVisitor<GenContext> flowGeneratorVisitor = new FlowGeneratorVisitor<>();

	private final boolean debug;

	public FlowToRXJavaVisitor(final Flow flow, final String packageName, final Template template,
			final boolean debug) {
		super(flow, packageName, template);
		this.debug = debug;
		flowGeneratorVisitor.register(new ProcessorCallGenerator(this, flowExecutionAttributes, flowExecutionBuilder, flowExecutionDependencies));
		flowGeneratorVisitor.register(new ConditionalFlowCtrlGenerator(this, flowExecutionAttributes, flowExecutionBuilder, flowExecutionDependencies));
	}

	public Template process() {

		availableVars.add(new CallVariable(Flow.ENTRY_POINT, flow.getEntryPointType(), Flow.ENTRY_POINT));

		super.processFlow();

		generateGlobalFlow();

		final var inputCall = flow.getCalls().stream().filter(Processor::isEntry).map(FlowToRXJavaVisitor::fieldNameOf)
				.collect(StreamExt.single()).orElseThrow(WrongCountException::new);

		final var templateProperties = new HashMap<String, String>();
		templateProperties.put("package", packageName);
		templateProperties.put("flow.name", flow.getName());
		templateProperties.put("flow.input", flow.getEntryPointType());
		templateProperties.put("flow.output", "void");
		templateProperties.put("flow.executionAttributes", flowExecutionAttributes.toString());
		templateProperties.put("flow.executionBuilder", flowExecutionBuilder.toString());
		templateProperties.put("flow.executionDependencies", flowExecutionDependencies.toString());
		templateProperties.put("flow.code", flowCode.toString());
		templateProperties.put("flow.start", inputCall);
		templateProperties.put("imports", imports.stream().map(i -> "import " + i + ";").collect(joining("\n")));
		return template.apply(templateProperties, JavaCodeGenerator.toSourceFilename(packageName, flow.getName()), null);
	}

	private void generateGlobalFlow() {
		Collections.reverse(processOrder);
		for (final var context : processOrder) {
			appendInfo(flowCode, context.processor).eol();

			final var varNameOfCall = fieldNameOf(context.processor);

			final var deps = context.getReverseDeps();
			flowCode.appendIndented("final var %s = triggerProcess(execution.%s, ", varNameOfCall, varNameOfCall);
			if (context.processor.isExit()) {
				flowCode.append("onFlowFinished");
			} else if (!deps.isEmpty()) {
				flowCode.append(
						deps.stream().map(d -> fieldNameOf(d) + "::subscribe").collect(joining(", ")));
			} else {
				flowCode.append("() -> {}");
			}
			flowCode.append(")").eos();
		}
	}

	@Override
	protected void process(final CallContext context) {
		flowExecutionAttributes.appendIndentedLine("// Processor call %s", context.getProcessorCall().asDataPoint());
		final var dependencies = flow.getAllDependencies(context.processor).stream()
				.sorted(Comparator.comparing(Processor::fromDataPoint)).toList();
		flowGeneratorVisitor.generateFlow(context, new AbstractFlowGenerator.GenContext(debug, dependencies));

	}

	static String fieldNameOf(final Processor processorCall) {
		return toVariable(processorCall) + "Call";
	}

	 static String fieldNameOf(CallContext context, Call call) {
		return (context.outputDataPoint + "_" + call.getCall()).replace('.', '_');
	}

}
