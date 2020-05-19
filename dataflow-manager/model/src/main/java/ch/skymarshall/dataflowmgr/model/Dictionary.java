package ch.skymarshall.dataflowmgr.model;

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

		public Calls<T> map(final String from, final String to) {
			final Calls<T> derivates = new Calls<>(kind, derivateFunc);
			callsName.entrySet().stream().filter(kv -> kv.getKey().startsWith(from + "."))
					.forEach(kv -> derivates.callsName.put(kv.getKey().substring(from.length() + 1),
							derivateFunc.apply(kv.getValue(), to)));
			return derivates;
		}

	}

	public final Calls<Processor> processors = new Calls<>("processor", Processor::derivate);

	public final Calls<Condition> conditions = new Calls<>("condition", Condition::derivate);

	public final Calls<ExternalAdapter> externalAdapters = new Calls<>("externalAdapter", ExternalAdapter::derivate);

}
