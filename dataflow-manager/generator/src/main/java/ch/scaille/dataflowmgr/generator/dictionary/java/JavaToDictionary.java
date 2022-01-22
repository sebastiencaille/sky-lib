package ch.scaille.dataflowmgr.generator.dictionary.java;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.stream.Collector;

import ch.scaille.dataflowmgr.annotations.Conditions;
import ch.scaille.dataflowmgr.annotations.ExternalAdapters;
import ch.scaille.dataflowmgr.annotations.Processors;
import ch.scaille.dataflowmgr.model.Dictionary;
import ch.scaille.generators.util.AbstractGenerator;
import ch.scaille.util.helpers.ClassFinder;
import ch.scaille.util.helpers.ClassFinder.Policy;

public class JavaToDictionary extends AbstractGenerator<Dictionary> {

	public static Collector<Class<?>, ?, Dictionary> toDictionary() {
		return toDictionary(JavaToDictionary::new);
	}

	public static ClassFinder configure(ClassFinder classFinder) {
		annotation2Handlers.keySet().forEach(a -> classFinder.withAnnotation(a, Policy.CLASS_ONLY));
		return classFinder;
	}

	private static final Map<Class<? extends Annotation>, BiConsumer<Dictionary, Class<?>>> annotation2Handlers = new HashMap<>();

	static {
		ProcessorToDictionary processorHandler = new ProcessorToDictionary();
		addAnnotation(Processors.class, processorHandler::addToDictionary);
		ExternalAdapterToDictionary externalAdapterHandler = new ExternalAdapterToDictionary();
		addAnnotation(ExternalAdapters.class, externalAdapterHandler::addToDictionary);
		CaseFlowCtrlToDictionary caseCtrlToDictionary = new CaseFlowCtrlToDictionary();
		addAnnotation(Conditions.class, caseCtrlToDictionary::addToDictionary);
	}

	public static void addAnnotation(Class<? extends Annotation> annotation,
			BiConsumer<Dictionary, Class<?>> annotatedClassHandler) {
		annotation2Handlers.put(annotation, annotatedClassHandler);
	}

	private final Dictionary dictionary = new Dictionary();

	public JavaToDictionary() {
	}

	public JavaToDictionary(Class<?>... classes) {
		super(classes);
	}

	@Override
	public Dictionary generate() {
		classes.forEach(this::handle);
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
