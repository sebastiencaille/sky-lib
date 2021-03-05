package ch.skymarshall.dataflowmgr.generator.dictionary.java;

import java.lang.reflect.Method;

import ch.skymarshall.dataflowmgr.annotations.ExternalAdapters;
import ch.skymarshall.dataflowmgr.model.Dictionary;
import ch.skymarshall.dataflowmgr.model.ExternalAdapter;

public class ExternalAdapterToDictionary extends AbstractClassToDictionary {

	public void addToDictionary(Dictionary dictionary, Class<?> clazz) {
		if (clazz.isAnnotationPresent(ExternalAdapters.class)) {
			methodsOf(clazz).forEach(m -> dictionary.externalAdapters.add(adapterFrom(m)));
		}
	}

	private ExternalAdapter adapterFrom(final Method m) {
		return new ExternalAdapter(methodFullName(m), m.getName(), parameters(m), returnType(m));
	}

}
