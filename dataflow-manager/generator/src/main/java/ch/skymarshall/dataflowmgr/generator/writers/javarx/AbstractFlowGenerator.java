package ch.skymarshall.dataflowmgr.generator.writers.javarx;

import java.util.List;

import ch.skymarshall.dataflowmgr.generator.writers.IFlowGenerator;
import ch.skymarshall.dataflowmgr.model.Binding;
import ch.skymarshall.util.generators.JavaCodeGenerator;

public abstract class AbstractFlowGenerator implements IFlowGenerator<AbstractFlowGenerator.GenContext> {

	public static class GenContext  {

		public final boolean debug;
		public final List<Binding> bindingDeps;
		public String topCall;

		public GenContext(boolean debug, List<Binding> bindingDeps) {
			this.debug = debug;
			this.bindingDeps = bindingDeps;
		}

	}

	protected final FlowToRXJavaVisitor visitor;

	protected final JavaCodeGenerator<RuntimeException> flowFactories;

	protected AbstractFlowGenerator(FlowToRXJavaVisitor visitor, JavaCodeGenerator<RuntimeException> generator) {
		this.visitor = visitor;
		this.flowFactories = generator;
	}

}
