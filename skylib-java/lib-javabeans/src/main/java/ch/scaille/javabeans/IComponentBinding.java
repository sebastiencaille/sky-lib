package ch.scaille.javabeans;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 * Unified access to a component's "property".
 * <p>
 *
 * @author Sebastien Caille
 *
 * @param <T> the type of the component's property
 */
@NullMarked
public interface IComponentBinding<T extends @Nullable Object> {

	/**
	 * Sets the component side of the value
     */
	void setComponentValue(final IComponentChangeSource source, final T value);

 	/**
	 * Called when bound to a link, so the component binding can hook to the
	 * component and forward it's content to the property
	 */
	default void addComponentValueChangeListener(final IComponentLink<T> link) { }


	default void removeComponentValueChangeListener() { }
}
