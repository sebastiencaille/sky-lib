package ch.skymarshall.dataflowmgr.generator.writer.javaproc;

import java.util.Iterator;

import ch.skymarshall.dataflowmgr.generator.AbstractFlowVisitor.BindingContext;
import ch.skymarshall.dataflowmgr.generator.IFlowGenerator;
import ch.skymarshall.dataflowmgr.model.Processor;
import ch.skymarshall.util.generators.JavaCodeGenerator;

public class ProcessorCallGenerator extends AbstractFlowGenerator {

	public ProcessorCallGenerator(FlowToProceduralJavaVisitor visitor, JavaCodeGenerator<RuntimeException> generator) {
		super(visitor, generator);
	}

	@Override
	public boolean matches(BindingContext context) {
		return true;
	}

	@Override
	public void generate(BindingContext context, Void fgContext, Iterator<IFlowGenerator<Void>> flowGeneratorIterator) {
		visitor.visitExternalAdapters(context, context.unprocessedAdapters(context.bindingAdapters));
		if (context.isExit()) {
			// Exit only has adapters
			return;
		}
		if (!visitor.definedDataPoints.contains(context.outputDataPoint)) {
			generateDataPoint(context.getProcessor(), context.outputDataPoint);
		} else {
			generator.appendIndented(context.outputDataPoint).append(" = ");
		}
		visitor.appendCall(context, context.getProcessor());
	}

	private void generateDataPoint(final Processor processor, final String outputDataPoint) {
		visitor.definedDataPoints.add(outputDataPoint);
		visitor.appendNewVariable(outputDataPoint, processor);
		generator.append(" = ");
	}

}
