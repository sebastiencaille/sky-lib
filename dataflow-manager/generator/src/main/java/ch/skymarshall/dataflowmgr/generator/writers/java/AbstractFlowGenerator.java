package ch.skymarshall.dataflowmgr.generator.writers.java;

import java.util.Iterator;

import ch.skymarshall.dataflowmgr.generator.AbstractFlowVisitor.BindingContext;
import ch.skymarshall.dataflowmgr.generator.procjavawriter.FlowToProceduralJavaVisitor;
import ch.skymarshall.util.generators.JavaCodeGenerator;

public abstract class AbstractFlowGenerator {

	protected final FlowToProceduralJavaVisitor visitor;

	protected final JavaCodeGenerator<RuntimeException> generator;

	public abstract boolean matches(BindingContext context);

	public abstract void generate(BindingContext context,  Iterator<AbstractFlowGenerator> flowGeneratorIterator);

	protected AbstractFlowGenerator(FlowToProceduralJavaVisitor visitor, JavaCodeGenerator<RuntimeException> generator) {
		this.visitor = visitor;
		this.generator = generator;
	}

}
