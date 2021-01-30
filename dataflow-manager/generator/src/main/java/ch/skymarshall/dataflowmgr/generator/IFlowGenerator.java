package ch.skymarshall.dataflowmgr.generator;

import java.util.Iterator;

import ch.skymarshall.dataflowmgr.generator.AbstractFlowVisitor.BindingContext;

public interface IFlowGenerator<C> {

	boolean matches(BindingContext context);

	void generate(BindingContext context,  C callContext, Iterator<IFlowGenerator<C>> flowGeneratorIterator);

}
