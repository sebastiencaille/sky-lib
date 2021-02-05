package ch.skymarshall.dataflowmgr.generator.writers;

import java.util.Iterator;
import java.util.List;

import ch.skymarshall.dataflowmgr.generator.writers.AbstractFlowVisitor.BindingContext;

public interface IFlowGenerator<C> {

	boolean matches(BindingContext context);

	void generate(BaseGenContext<C> genContext, BindingContext context);

	public static class BaseGenContext<C> {

		private final Iterator<IFlowGenerator<C>> flowGeneratorIterator;
		private C localContext;
		
		public BaseGenContext(List<IFlowGenerator<C>> flowGenerators, C localContext) {
			this.flowGeneratorIterator = flowGenerators.iterator();
			this.localContext=localContext;
		}

		public void next(BindingContext context) {
			if (flowGeneratorIterator.hasNext()) {
				flowGeneratorIterator.next().generate(this, context);
			}
		}
		
		public C getLocalContext() {
			return localContext;
		}
		
		public void setLocalContext(C localContext) {
			this.localContext = localContext;
		}
		
	}

}
