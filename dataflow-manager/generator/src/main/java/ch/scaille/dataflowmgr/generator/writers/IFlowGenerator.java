package ch.scaille.dataflowmgr.generator.writers;

import java.util.Iterator;
import java.util.List;

import ch.scaille.dataflowmgr.generator.writers.AbstractFlowVisitor.CallContext;

public interface IFlowGenerator<C> {

	boolean matches(CallContext context);

	void generate(BaseGenContext<C> genContext, CallContext context);

	class BaseGenContext<C> {

		private final Iterator<IFlowGenerator<C>> flowGeneratorIterator;
		private C localContext;

		/**
		 * @param flowGenerators The list of generators to apply
		 */
		public BaseGenContext(List<IFlowGenerator<C>> flowGenerators, C localContext) {
			this.flowGeneratorIterator = flowGenerators.iterator();
			this.localContext = localContext;
		}

		public void run(CallContext context) {
			while (flowGeneratorIterator.hasNext()) {
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
