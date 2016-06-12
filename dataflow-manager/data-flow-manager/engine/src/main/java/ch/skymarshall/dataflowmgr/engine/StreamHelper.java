package ch.skymarshall.dataflowmgr.engine;

import java.util.Optional;
import java.util.function.IntFunction;
import java.util.stream.Collector;

public class StreamHelper {

	public static class ZeroOrOne<T> {
		private Optional<T> value = Optional.empty();
		private int count;

		public T getValue() {
			return value.orElse(null);
		}

		void setValue(final T newValue) {
			if (newValue == null) {
				return;
			}
			count++;
			if (!value.isPresent()) {
				this.value = Optional.of(newValue);
			}
		}

		public <E extends Exception> Optional<T> orElseThrow(final IntFunction<E> supplier) throws E {
			if (count > 1) {
				throw supplier.apply(count);
			}
			return value;
		}
	}

	public static <E> Collector<E, ?, ZeroOrOne<E>> zeroOrOne() {
		return Collector.of(ZeroOrOne::new, ZeroOrOne::setValue, (left, right) -> {
			left.setValue(right.getValue());
			return left;
		});
	}

}
