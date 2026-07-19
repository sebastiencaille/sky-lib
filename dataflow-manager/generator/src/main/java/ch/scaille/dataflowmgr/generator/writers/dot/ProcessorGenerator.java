package ch.scaille.dataflowmgr.generator.writers.dot;

import ch.scaille.dataflowmgr.generator.writers.AbstractFlowVisitor.CallContext;
import ch.scaille.dataflowmgr.generator.writers.dot.FlowToDotVisitor.Graph;
import ch.scaille.dataflowmgr.generator.writers.dot.FlowToDotVisitor.Link;

public class ProcessorGenerator extends AbstractDotFlowGenerator {

	protected ProcessorGenerator(FlowToDotVisitor visitor, Graph graph) {
		super(visitor, graph);
	}

	@Override
	public boolean matches(CallContext context) {
		return true;
	}

	@Override
	public void generate(BaseGenContext<String> genContext, CallContext context) {

		final var processorNode = visitor.addProcessor(context.processor, context.getProcessorCall());
		final var missingAdapters = context.unprocessedAdapters(context.callAdapters);
		visitor.addExternalAdapters(missingAdapters, genContext.getLocalContext(), processorNode);

		graph.links().add(new Link(processorNode, context.outputDataPoint));
		genContext.run(context);
	}

}
