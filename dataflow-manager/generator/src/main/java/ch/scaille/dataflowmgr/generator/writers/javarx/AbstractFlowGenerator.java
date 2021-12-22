package ch.scaille.dataflowmgr.generator.writers.javarx;

import java.util.List;

import ch.scaille.dataflowmgr.generator.writers.IFlowGenerator;
import ch.scaille.dataflowmgr.model.Binding;
import ch.scaille.util.generators.JavaCodeGenerator;

public abstract class AbstractFlowGenerator implements IFlowGenerator<AbstractFlowGenerator.GenContext> {

	public static class GenContext {

		public final boolean debug;
		public final List<Binding> bindingDeps;
		private String topCall;

		public GenContext(boolean debug, List<Binding> bindingDeps) {
			this.debug = debug;
			this.bindingDeps = bindingDeps;
		}

		public String getTopCall() {
			return topCall;
		}

		public void setTopCall(String topCall) {
			this.topCall = topCall;
		}

	}

	protected final FlowToRXJavaVisitor visitor;

	protected final JavaCodeGenerator<RuntimeException> flowFactories;

	protected AbstractFlowGenerator(FlowToRXJavaVisitor visitor, JavaCodeGenerator<RuntimeException> generator) {
		this.visitor = visitor;
		this.flowFactories = generator;
	}

}
