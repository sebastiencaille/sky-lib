package ch.scaille.dataflowmgr.generator.writers.dot;

import ch.scaille.dataflowmgr.generator.writers.IFlowGenerator;
import ch.scaille.dataflowmgr.generator.writers.dot.FlowToDotVisitor.Graph;

public abstract class AbstractDotFlowGenerator implements IFlowGenerator<String> {

	protected final FlowToDotVisitor visitor;
	protected final Graph graph;

	protected AbstractDotFlowGenerator(FlowToDotVisitor visitor, Graph graph) {
		this.visitor = visitor;
		this.graph = graph;
	}

}
