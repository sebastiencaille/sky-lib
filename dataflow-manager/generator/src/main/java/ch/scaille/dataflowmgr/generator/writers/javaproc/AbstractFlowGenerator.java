package ch.scaille.dataflowmgr.generator.writers.javaproc;

import ch.scaille.dataflowmgr.generator.writers.IFlowGenerator;
import ch.scaille.generators.util.JavaCodeGenerator;

public abstract class AbstractFlowGenerator implements IFlowGenerator<Void> {

	protected final FlowToProceduralJavaVisitor visitor;

	protected final JavaCodeGenerator<RuntimeException> generator;

	protected AbstractFlowGenerator(FlowToProceduralJavaVisitor visitor,
			JavaCodeGenerator<RuntimeException> generator) {
		this.visitor = visitor;
		this.generator = generator;
	}

}
