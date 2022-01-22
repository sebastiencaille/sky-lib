package ch.scaille.dataflowmgr.generator.dictionary.java;

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

	private final Map<Class<? extends Annotation>, BiConsumer<Dictionary, Class<?>>> annotation2Handlers = new HashMap<>();

	private final ClassFinder classFinder = ClassFinder.forApp();

	private final Dictionary dictionary = new Dictionary();

	public JavaToDictionary() {
		ProcessorToDictionary processorHandler = new ProcessorToDictionary();
		addAnnotation(Processors.class, processorHandler::addToDictionary);
		ExternalAdapterToDictionary externalAdapterHandler = new ExternalAdapterToDictionary();
		addAnnotation(ExternalAdapters.class, externalAdapterHandler::addToDictionary);
		CaseFlowCtrlToDictionary caseCtrlToDictionary = new CaseFlowCtrlToDictionary();
		addAnnotation(Conditions.class, caseCtrlToDictionary::addToDictionary);
	}

	public JavaToDictionary addAnnotation(Class<? extends Annotation> annotation,
			BiConsumer<Dictionary, Class<?>> annotatedClassHandler) {
		classFinder.withAnnotation(annotation, Policy.CLASS_ONLY);
		annotation2Handlers.put(annotation, annotatedClassHandler);
		return this;
	}

	public Dictionary scan(final String apiClassPackage) {
		classFinder.withPackages(apiClassPackage).scan().forEach(this::handle);
		return dictionary;
	}

	private void handle(Class<?> clazz) {
		for (Annotation annotation : clazz.getAnnotations()) {
			if (annotation2Handlers.containsKey(annotation.annotationType())) {
				annotation2Handlers.get(annotation.annotationType()).accept(dictionary, clazz);
			}
		}
	}
}
