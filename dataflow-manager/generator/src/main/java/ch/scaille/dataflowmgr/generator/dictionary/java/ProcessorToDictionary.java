package ch.scaille.dataflowmgr.generator.dictionary.java;

import java.lang.reflect.Method;

import ch.scaille.dataflowmgr.annotations.Processors;
import ch.scaille.dataflowmgr.model.Dictionary;
import ch.scaille.dataflowmgr.model.ProcessorCall;

public class ProcessorToDictionary extends AbstractClassToDictionary {

	public void addToDictionary(Dictionary dictionary, Class<?> clazz) {
		if (clazz.isAnnotationPresent(Processors.class)) {
			System.out.println("Processor " + clazz);
			methodsOf(clazz).forEach(m -> dictionary.processors.add(processorFrom(m)));
		}
	}

	private ProcessorCall processorFrom(final Method m) {
		System.out.println("Processor method " + m.getName());
		return new ProcessorCall(methodFullName(m), m.getName(), parameters(m), returnType(m));
	}

}
