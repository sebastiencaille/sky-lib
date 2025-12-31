package ch.scaille.gui.mvc;

import ch.scaille.javabeans.IComponentBinding;
import ch.scaille.javabeans.IComponentChangeSource;
import ch.scaille.javabeans.IComponentLink;
import org.jspecify.annotations.NonNull;

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
	public void addComponentValueChangeListener(@NonNull final IComponentLink<T> converter) {
		// no op
	}

	@Override
	public void removeComponentValueChangeListener() {
		// no op
	}

	@Override
	public void setComponentValue(@NonNull final IComponentChangeSource source, final T value) {
		// no op
	}

}
