package ch.scaille.tcwriter.server.services;

import org.jspecify.annotations.Nullable;

import java.util.Optional;
import java.util.function.Supplier;


/**
 * Allows getting/setting a pre-defined attribute from/to the session
 * 
 * @param <T> the type of the attribute
 */
public class SessionGetSet<T> {

	private final SessionAccessor accessor;
	private final String attribName;
	@Nullable
	private final Supplier<T> defaultValue;

	public SessionGetSet(SessionAccessor accessor, String attribName, @Nullable Supplier<T> defaultValue) {
		this.accessor = accessor;
		this.attribName = attribName;
		this.defaultValue = defaultValue;
	}

	/**
	 * Gets the value of the attribute
	 * 
	 * @return the value, or null if not present
	 */
	public Optional<T> get() {
		return accessor.get(attribName);
	}

	public T orElseGet(Supplier<T> orElse) {
		return get().orElseGet(orElse);
	}

	public T mandatory() {
		final var found = get();
		if (defaultValue == null) {
			return found.orElseThrow(() -> new IllegalStateException("Attribute " + attribName + " was not found"));
		}
		return found.orElseGet(defaultValue);
	}

	public void set(@Nullable T value) {
		if (value != null) {
			accessor.set(attribName, value);
		} else {
			accessor.remove(attribName);
		}
	}
	
	public void remove() {
		accessor.remove(attribName);
	}

}