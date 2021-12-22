package ch.scaille.dataflowmgr.generator.writers;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;

import ch.scaille.dataflowmgr.generator.writers.AbstractFlowVisitor.BindingContext;
import ch.scaille.dataflowmgr.generator.writers.IFlowGenerator.BaseGenContext;

public class FlowGeneratorVisitor<T> {

	protected final List<IFlowGenerator<T>> flowGenerators = new ArrayList<>();

	public void register(IFlowGenerator<T> flowCtrl) {
		flowGenerators.add(flowCtrl);
	}

	public void generateFlow(final BindingContext context, T genContext) {
		new BaseGenContext<>(getFlowGenerators(context), genContext).next(context);
	}

	private List<IFlowGenerator<T>> getFlowGenerators(BindingContext context) {
		return flowGenerators.stream().filter(g -> g.matches(context)).collect(toList());
	}

}
