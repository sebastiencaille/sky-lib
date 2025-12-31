package ch.scaille.dataflowmgr.generator.writers.dot;

import ch.scaille.dataflowmgr.generator.writers.AbstractFlowVisitor.BindingContext;
import ch.scaille.dataflowmgr.generator.writers.dot.FlowToDotVisitor.Graph;
import ch.scaille.dataflowmgr.generator.writers.dot.FlowToDotVisitor.Link;

public class ProcessorGenerator extends AbstractDotFlowGenerator {

	protected ProcessorGenerator(FlowToDotVisitor visitor, Graph graph) {
		super(visitor, graph);
	}

	@Override
	public boolean matches(BindingContext context) {
		return true;
	}

	@Override
	public void generate(BaseGenContext<String> genContext, BindingContext context) {

		final var processorNode = visitor.addProcessor(context.binding, context.getProcessor());
		final var missingAdapters = context.unprocessedAdapters(context.bindingAdapters);
		visitor.addExternalAdapters(missingAdapters, genContext.getLocalContext(), processorNode);

		graph.links().add(new Link(processorNode, context.outputDataPoint));
		genContext.next(context);
	}

}
