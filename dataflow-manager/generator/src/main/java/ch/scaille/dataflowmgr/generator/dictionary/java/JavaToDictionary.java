package ch.scaille.dataflowmgr.generator.dictionary.java;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import ch.scaille.dataflowmgr.annotations.Conditions;
import ch.scaille.dataflowmgr.annotations.ExternalAdapters;
import ch.scaille.dataflowmgr.annotations.Processors;
import ch.scaille.dataflowmgr.model.Dictionary;
import ch.scaille.util.helpers.ClassFinder;
import ch.scaille.util.helpers.ClassFinder.Policy;

public class JavaToDictionary {

	private final ClassFinder classFinder = ClassFinder.forApp();

	private final Map<Class<? extends Annotation>, BiConsumer<Dictionary, Class<?>>> annotation2Handlers = new HashMap<>();

	public JavaToDictionary() {
		ProcessorToDictionary processorHandler = new ProcessorToDictionary();
		addAnnotation(Processors.class, processorHandler::addToDictionary);
		ExternalAdapterToDictionary externalAdapterHandler = new ExternalAdapterToDictionary();
		addAnnotation(ExternalAdapters.class, externalAdapterHandler::addToDictionary);
		CaseFlowCtrlToDictionary caseCtrlToDictionary = new CaseFlowCtrlToDictionary();
		addAnnotation(Conditions.class, caseCtrlToDictionary::addToDictionary);
	}

	public void addAnnotation(Class<? extends Annotation> annotation,
			BiConsumer<Dictionary, Class<?>> annotatedClassHandler) {
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
