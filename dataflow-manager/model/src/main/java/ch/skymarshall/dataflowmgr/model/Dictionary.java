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

	public Processor getProcessor(final String name) {
		final Processor processor = processors.get(name);
		if (processor == null) {
			throw new InvalidParameterException("name: " + name);
		}
		return processor;
	}

	public ExternalAdapter getExternalAdapter(final String name) {
		final ExternalAdapter adapter = externalAdapters.get(name);
		if (adapter == null) {
			throw new InvalidParameterException("name: " + name);
		}
		return adapter;
	}

	public void mapToService(final String from, final String to) {
		map(processors, from, to, Processor::derivate);
		map(externalAdapters, from, to, ExternalAdapter::derivate);
	}

	private static <T> void map(final Map<String, T> original, final String from, final String to,
			final BiFunction<T, String, T> derivate) {
		final Map<String, T> copy = new HashMap<>(original);
		copy.entrySet().stream().filter(kv -> kv.getKey().startsWith(from + "."))
				.forEach(kv -> original.put(kv.getKey().replaceAll(from, to), derivate.apply(kv.getValue(), to)));
	}

}
