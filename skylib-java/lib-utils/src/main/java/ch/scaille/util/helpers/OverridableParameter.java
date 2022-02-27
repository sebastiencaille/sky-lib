package ch.scaille.util.helpers;

import java.util.Optional;
import java.util.function.Function;

public class OverridableParameter<S, T> {

	private Function<S, T> defaultProvider;

	private S source;

	private Optional<T> value = Optional.empty();

	public OverridableParameter(Function<S, T> defaultProvider) {
		this.defaultProvider = defaultProvider;
	}

	public OverridableParameter<S, T> withSource(S source) {
		this.source = source;
		return this;
	}

	public void set(T value) {
		this.value = Optional.of(value);
	}

	public T get() {
		ensureLoaded();
		return this.value.get();
	}

	public void ensureLoaded() {
		if (this.value.isPresent()) {
			return;
		}
		if (source == null) {
			throw new IllegalStateException("Source not available");
		}
		this.value = Optional.of(defaultProvider.apply(source));
	}

}
