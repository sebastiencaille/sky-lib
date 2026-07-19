package ch.scaille.dataflowmgr.model;

import static java.util.stream.Collectors.toMap;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;

public class Dictionary {

	public static class Calls<T extends Call> {
		private final Map<String, T> callsByName = new HashMap<>();
		private final String kind;
		private final BiFunction<T, String, T> derivateFunc;

		public Calls(final String kind, final BiFunction<T, String, T> derivateFunc) {
			this.kind = kind;
			this.derivateFunc = derivateFunc;
		}

		public void add(final T call) {
			callsByName.put(call.getName(), call);
		}

		public T get(final Mapper mapper) {
			return Objects.requireNonNull(callsByName.get(mapper.symbol()),
					"No " + kind + " found: " + mapper.symbol() + " in " + callsByName.keySet());
		}

		// Maps the service to the instance available in the target code
		public Calls<T> map(Mapper mapper) {
			final var derivates = new Calls<>(kind, derivateFunc);
			final var allDerivates = callsByName.entrySet().stream().filter(kv -> kv.getKey().startsWith(mapper.symbol() + "."))
					.collect(toMap(kv -> kv.getKey().substring(mapper.symbol().length() + 1),
							kv -> derivateFunc.apply(kv.getValue(), mapper.alias())));
			derivates.callsByName.putAll(allDerivates);
			return derivates;
		}

		@Override
		public String toString() {
			return kind + ": " + callsByName.keySet();
		}
		
	}

	public final Calls<ProcessorCall> processors = new Calls<>("processor", ProcessorCall::derivate);

	public final Calls<ExternalAdapter> externalAdapters = new Calls<>("externalAdapter", ExternalAdapter::derivate);

	public final Map<Class<?>, Calls<?>> flowControl = new HashMap<>();
	
	@Override
	public String toString() {
		return "%s, %s".formatted(processors, externalAdapters);
	}
}
