package ch.skymarshall.dataflowmgr.model;

import static java.util.stream.Collectors.toMap;

import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public class Dictionary {

	public static class Calls<T extends Call<T>> {
		private final Map<String, T> callsName = new HashMap<>();
		private final String kind;
		private final BiFunction<T, String, T> derivateFunc;

		public Calls(final String kind, final BiFunction<T, String, T> derivateFunc) {
			this.kind = kind;
			this.derivateFunc = derivateFunc;
		}

		public void add(final T call) {
			callsName.put(call.getName(), call);
		}

		public T get(final String name) {
			return callsName.computeIfAbsent(name, n -> {
				throw new InvalidParameterException("No " + kind + " found: " + n);
			});
		}

		// Maps the service to the instance available in the target code
		public Calls<T> map(final String from, final String to) {
			final Calls<T> derivates = new Calls<>(kind, derivateFunc);
			final Map<String, T> allDerivates = callsName.entrySet().stream()
					.filter(kv -> kv.getKey().startsWith(from + "."))
					.collect(toMap(kv -> kv.getKey().substring(from.length() + 1),
							kv -> derivateFunc.apply(kv.getValue(), to)));
			derivates.callsName.putAll(allDerivates);
			return derivates;
		}

	}


	public final Calls<Processor> processors = new Calls<>("processor", Processor::derivate);

	public final Calls<ExternalAdapter> externalAdapters = new Calls<>("externalAdapter", ExternalAdapter::derivate);

	public final Map<Class<?>, Calls<?>> flowControl = new HashMap<>();
}
