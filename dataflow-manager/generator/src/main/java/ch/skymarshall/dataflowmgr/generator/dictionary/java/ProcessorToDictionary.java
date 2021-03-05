package ch.skymarshall.dataflowmgr.generator.dictionary.java;

import java.lang.reflect.Method;

import ch.skymarshall.dataflowmgr.annotations.Processors;
import ch.skymarshall.dataflowmgr.model.Dictionary;
import ch.skymarshall.dataflowmgr.model.Processor;

public class ProcessorToDictionary extends AbstractClassToDictionary {

	public void addToDictionary(Dictionary dictionary, Class<?> clazz) {
		if (clazz.isAnnotationPresent(Processors.class)) {
			methodsOf(clazz).forEach(m -> dictionary.processors.add(processorFrom(m)));
		}
	}

	private Processor processorFrom(final Method m) {
		return new Processor(methodFullName(m), m.getName(), parameters(m), returnType(m));
	}

}
