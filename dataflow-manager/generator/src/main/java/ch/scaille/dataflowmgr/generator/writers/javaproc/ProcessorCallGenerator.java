package ch.scaille.dataflowmgr.generator.writers.javaproc;

import ch.scaille.dataflowmgr.generator.writers.AbstractFlowVisitor.CallContext;
import ch.scaille.dataflowmgr.model.ProcessorCall;
import ch.scaille.generators.util.JavaCodeGenerator;

public class ProcessorCallGenerator extends AbstractFlowGenerator {

	public ProcessorCallGenerator(FlowToProceduralJavaVisitor visitor, JavaCodeGenerator<RuntimeException> generator) {
		super(visitor, generator);
	}

	@Override
	public boolean matches(CallContext context) {
		return true;
	}

	@Override
	public void generate(BaseGenContext<Void> genContext, CallContext context) {
		visitor.visitExternalAdapters(context, context.unprocessedAdapters(context.callAdapters));
		if (context.isExit()) {
			// Exit only has adapters
			return;
		}
		if (!visitor.definedDataPoints.contains(context.outputDataPoint)) {
			generateDataPoint(context.getProcessorCall(), context.outputDataPoint);
		} else {
			generator.appendIndented(context.outputDataPoint).append(" = ");
		}
		visitor.appendCall(context, context.getProcessorCall());
		genContext.run(context);
	}

	private void generateDataPoint(final ProcessorCall processorCall, final String outputDataPoint) {
		visitor.definedDataPoints.add(outputDataPoint);
		visitor.appendNewVariable(outputDataPoint, processorCall);
		generator.append(" = ");
	}

}
