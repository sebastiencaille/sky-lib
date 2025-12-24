package ch.scaille.util.helpers;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.function.Function;

/**
 * Allows to override a configurable default value
 * @param <S>
 * @param <T>
 */
public class OverridableParameter<S, T> {

	@NonNull
	private final Function<S, T> defaultProvider;

	private S source = null;

	private T value = null;

	public OverridableParameter(@NonNull Function<S, T> defaultProvider) {
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

	public @NonNull T get() {
		ensureLoaded();
		return this.value;
	}

	public void ensureLoaded() {
		if (this.value != null) {
			return;
		}
		this.value = defaultProvider.apply(Objects.requireNonNull(source, "No source defined"));
	}

}
