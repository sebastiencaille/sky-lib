package ch.scaille.gui.mvc;

import ch.scaille.javabeans.IComponentBinding;
import ch.scaille.javabeans.IComponentLink;
import ch.scaille.javabeans.properties.AbstractProperty;

/**
 * Parent class of all component bindings.
 * <p>
 *
 * @author Sebastien Caille
 *
 * @param <T>
 */
public class ComponentBindingAdapter<T> implements IComponentBinding<T> {

	@Override
	public void addComponentValueChangeListener(final IComponentLink<T> converter) {
		// no op
	}

	@Override
	public void removeComponentValueChangeListener() {
		// no op
	}

	@Override
	public void setComponentValue(final AbstractProperty source, final T value) {
		// no op
	}

}
