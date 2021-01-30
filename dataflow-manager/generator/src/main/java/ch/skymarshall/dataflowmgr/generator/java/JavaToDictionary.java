package ch.skymarshall.dataflowmgr.generator.java;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import ch.skymarshall.dataflowmgr.annotations.Conditions;
import ch.skymarshall.dataflowmgr.annotations.ExternalAdapters;
import ch.skymarshall.dataflowmgr.annotations.Processors;
import ch.skymarshall.dataflowmgr.model.Dictionary;
import ch.skymarshall.util.helpers.ClassFinder;
import ch.skymarshall.util.helpers.ClassFinder.Policy;

public class JavaToDictionary {

	private final ClassFinder classFinder = ClassFinder.forApp();

	private final Map<Class<? extends Annotation>, BiConsumer<Dictionary, Class<?>>> annotation2Handlers = new HashMap<>();

	public JavaToDictionary() {
		ProcessorToDictionary processorHandler = new ProcessorToDictionary();
		addAnnotation(Processors.class,  processorHandler::addToDictionary);
		ExternalAdapterToDictionary externalAdapterHandler = new ExternalAdapterToDictionary();
		addAnnotation(ExternalAdapters.class, externalAdapterHandler::addToDictionary);
		CaseFlowCtrlToDictionary caseCtrlToDictionary = new CaseFlowCtrlToDictionary();
		addAnnotation(Conditions.class, caseCtrlToDictionary::addToDictionary);
	}

	public void addAnnotation(Class<? extends Annotation>  annotation, BiConsumer<Dictionary, Class<?>> annotatedClassHandler) {
		classFinder.addExpectedAnnotation(annotation, Policy.CLASS_ONLY);
		annotation2Handlers.put(annotation, annotatedClassHandler);
	}

	public Dictionary scan(final String apiClassPackage) throws IOException {
		final Dictionary dictionary = new Dictionary();
		classFinder.addExpectedAnnotation(Processors.class, Policy.CLASS_ONLY);
		classFinder.addExpectedAnnotation(ExternalAdapters.class, Policy.CLASS_ONLY);
		classFinder.addExpectedAnnotation(Conditions.class, Policy.CLASS_ONLY);

		for (final Class<?> clazz : classFinder.collect(apiClassPackage).getResult()) {
			for (Annotation annotation : clazz.getAnnotations()) {
				if (annotation2Handlers.containsKey(annotation.annotationType())) {
					annotation2Handlers.get(annotation.annotationType()).accept(dictionary, clazz);
				}
			}
		}
		return dictionary;
	}

}
