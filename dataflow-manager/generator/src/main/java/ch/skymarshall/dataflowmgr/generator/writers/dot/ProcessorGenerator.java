package ch.skymarshall.dataflowmgr.generator.writers.dot;

import java.util.Set;

import ch.skymarshall.dataflowmgr.generator.writers.AbstractFlowVisitor.BindingContext;
import ch.skymarshall.dataflowmgr.generator.writers.dot.FlowToDotVisitor.Graph;
import ch.skymarshall.dataflowmgr.generator.writers.dot.FlowToDotVisitor.Link;
import ch.skymarshall.dataflowmgr.model.ExternalAdapter;

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

		final String processorNode = visitor.addProcessor(context.binding, context.getProcessor());

		Set<ExternalAdapter> missingAdapters = context.unprocessedAdapters(context.bindingAdapters);
		visitor.addExternalAdapters(missingAdapters, genContext.getLocalContext(), processorNode);

		graph.links.add(new Link(processorNode, context.outputDataPoint));
		genContext.next(context);
	}

}
