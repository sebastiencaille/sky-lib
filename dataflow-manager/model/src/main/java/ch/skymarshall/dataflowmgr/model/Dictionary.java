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

	public static class Service {

		private final Map<String, Processor> processors = new HashMap<>();

		private final Map<String, ExternalAdapter> externalAdapters = new HashMap<>();

		private final String serviceName;

		public Service(final String serviceName) {
			this.serviceName = serviceName;
		}

		public Processor getProcessor(final String name) {
			return processors.computeIfAbsent(name, n -> {
				throw new InvalidParameterException("No parameter found: " + n);
			});
		}

		public ExternalAdapter getExternalAdapter(final String name) {
			return externalAdapters.computeIfAbsent(name, n -> {
				throw new InvalidParameterException("No adapter found: " + n);
			});
		}

		private <T> void map(final Map<String, T> original, final Map<String, T> target, final String from,
				final BiFunction<T, String, T> derivate) {
			original.entrySet().stream().filter(kv -> kv.getKey().startsWith(from + ".")).forEach(kv -> target
					.put(kv.getKey().substring(from.length() + 1), derivate.apply(kv.getValue(), serviceName)));
		}
	}

	public Service mapApiToService(final String from, final String to) {
		final Service service = new Service(to);
		service.map(processors, service.processors, from, Processor::derivate);
		service.map(externalAdapters, service.externalAdapters, from, ExternalAdapter::derivate);
		return service;
	}

}
