package ch.skymarshall.dataflowmgr.generator;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.stream.Stream;

import ch.skymarshall.dataflowmgr.annotations.ExternalAdapter;
import ch.skymarshall.dataflowmgr.annotations.Processor;
import ch.skymarshall.dataflowmgr.model.Dictionary;
import ch.skymarshall.util.helpers.ClassFinder;
import ch.skymarshall.util.helpers.ClassFinder.Policy;

public class JavaToDictionary {

	private final ClassFinder classFinder = ClassFinder.forApp();

	public Dictionary scan(final String apiClassPackage) throws IOException {
		final Dictionary dictionary = new Dictionary();
		classFinder.addExpectedAnnotation(Processor.class, Policy.CLASS_ONLY);
		classFinder.addExpectedAnnotation(ExternalAdapter.class, Policy.CLASS_ONLY);

		for (final Class<?> clazz : classFinder.collect(apiClassPackage).getResult()) {
			if (clazz.isAnnotationPresent(Processor.class)) {
				streamOf(clazz)
						.forEach(m -> dictionary.addProcessor(ch.skymarshall.dataflowmgr.model.Processor.from(m)));
			} else if (clazz.isAnnotationPresent(ExternalAdapter.class)) {
				streamOf(clazz).forEach(
						m -> dictionary.addExternalAdapter(ch.skymarshall.dataflowmgr.model.ExternalAdapter.from(m)));
			}
		}
		return dictionary;
	}

	private Stream<Method> streamOf(final Class<?> clazz) {
		return Stream.of(clazz.getMethods()).filter(m -> !Object.class.equals(m.getDeclaringClass()));
	}

}
