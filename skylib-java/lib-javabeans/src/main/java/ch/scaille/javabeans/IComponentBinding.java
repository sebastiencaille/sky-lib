package ch.scaille.javabeans;

import ch.scaille.javabeans.properties.AbstractProperty;

/**
 * Unified access to a component's "property".
 * <p>
 *
 * @author Sebastien Caille
 *
 * @param <T> the type of the component's property
 */
public interface IComponentBinding<T> {

	/**
	 * Called when bound to a link, so the component binding can hook to the
	 * component and forward it's content to the property
	 */
	void addComponentValueChangeListener(final IComponentLink<T> link);

	/**
	 *
     */
	void setComponentValue(final AbstractProperty source, final T value);

	void removeComponentValueChangeListener();
}
