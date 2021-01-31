package ch.skymarshall.dataflowmgr.generator.writers.javaproc;

import ch.skymarshall.dataflowmgr.generator.writers.IFlowGenerator;
import ch.skymarshall.util.generators.JavaCodeGenerator;

public abstract class AbstractFlowGenerator implements IFlowGenerator<Void> {

	protected final FlowToProceduralJavaVisitor visitor;

	protected final JavaCodeGenerator<RuntimeException> generator;

	protected AbstractFlowGenerator(FlowToProceduralJavaVisitor visitor,
			JavaCodeGenerator<RuntimeException> generator) {
		this.visitor = visitor;
		this.generator = generator;
	}

}
