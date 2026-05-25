package ch.scaille.util.helpers;

import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.function.Function;

/**
 * Allows to override a configurable default value
 * @param <S>
 * @param <T>
 */
public class OverridableParameter<S, T> {

	private final Function<S, T> defaultProvider;

	@Nullable
	private S source = null;

	@Nullable
	private T value = null;

	public OverridableParameter(Function<S, T> defaultProvider) {
		this.defaultProvider = defaultProvider;
	}

	public OverridableParameter<S, T> withSource(S source) {
		this.source = source;
		return this;
	}

	public OverridableParameter<S, T> set(@Nullable T value) {
		this.value = value;
		return this;
	}

	public T get() {
		ensureLoaded();
		return Objects.requireNonNull(this.value);
	}

	public void ensureLoaded() {
		if (this.value == null) {
			this.value = defaultProvider.apply(Objects.requireNonNull(source, "No source defined"));
		}
	}

}
