package ch.skymarshall.dataflowmgr.generator;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ch.skymarshall.dataflowmgr.generator.AbstractFlowVisitor.BindingContext;

public class FlowGeneratorVisitor<T> {

	protected final List<IFlowGenerator<T>> flowGenerators = new ArrayList<>();
	
	public void registerFlowGenerator(IFlowGenerator<T> flowCtrl) {
		flowGenerators.add(flowCtrl);
	}

	public void generateFlow(final BindingContext context, T fgContext) {
		List<IFlowGenerator<T>> flowGenerator = getFlowGenerators(context);
		Iterator<IFlowGenerator<T>> flowGeneratorIterator = flowGenerator.iterator();
		flowGeneratorIterator.next().generate(context, fgContext, flowGeneratorIterator);
	}

	private List<IFlowGenerator<T>> getFlowGenerators(BindingContext context) {
		return flowGenerators.stream().filter(g -> g.matches(context)).collect(toList());
	}

}
