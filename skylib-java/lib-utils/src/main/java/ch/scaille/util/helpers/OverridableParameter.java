package ch.scaille.util.helpers;

import java.util.function.Function;

/**
 * Allows to override a configurable default value
 * @param <S>
 * @param <T>
 */
public class OverridableParameter<S, T> {

	private final Function<S, T> defaultProvider;

	private S source;

	private T value = null;

	public OverridableParameter(Function<S, T> defaultProvider) {
		this.defaultProvider = defaultProvider;
	}

	public OverridableParameter<S, T> withSource(S source) {
		this.source = source;
		return this;
	}

	public void set(T value) {
		this.value = value;
	}

	public T get() {
		ensureLoaded();
		return this.value;
	}

	public void ensureLoaded() {
		if (this.value != null) {
			return;
		}
		if (source == null) {
			throw new IllegalStateException("Source not available");
		}
		this.value = defaultProvider.apply(source);
	}

}
