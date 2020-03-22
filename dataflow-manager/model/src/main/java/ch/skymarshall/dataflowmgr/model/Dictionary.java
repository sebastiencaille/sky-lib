package ch.skymarshall.dataflowmgr.model;

import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public class Dictionary {

	private final Map<String, Processor> processors = new HashMap<>();

	private final Map<String, ExternalAdapter> externalAdapters = new HashMap<>();

	public void addProcessor(final Processor processor) {
		processors.put(processor.getName(), processor);
	}

	public void addExternalAdapter(final ExternalAdapter externalInput) {
		externalAdapters.put(externalInput.getName(), externalInput);
	}

	public Processor getProcessor(final String clazz, final String name) {
		return processors.computeIfAbsent(clazz + '.' + name, n -> {
			throw new InvalidParameterException("No parameter found: " + n);
		});
	}

	public ExternalAdapter getExternalAdapter(final String clazz, final String name) {
		return externalAdapters.computeIfAbsent(clazz + '.' + name, n -> {
			throw new InvalidParameterException("No adapter found: " + n);
		});
	}

	public void mapToService(final String from, final String to) {
		map(processors, from, to, Processor::derivate);
		map(externalAdapters, from, to, ExternalAdapter::derivate);
	}

	private static <T> void map(final Map<String, T> original, final String from, final String to,
			final BiFunction<T, String, T> derivate) {
		final Map<String, T> copy = new HashMap<>(original);
		copy.entrySet().stream().filter(kv -> kv.getKey().startsWith(from + "."))
				.forEach(kv -> original.put(kv.getKey(), derivate.apply(kv.getValue(), to)));
	}

}
