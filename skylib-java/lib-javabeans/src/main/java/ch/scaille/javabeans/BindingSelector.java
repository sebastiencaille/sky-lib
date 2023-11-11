package ch.scaille.javabeans;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import ch.scaille.javabeans.properties.AbstractProperty;

/**
 * Allows enabling some properties on a per-object basis.
 * <p>
 *
 * @author Sebastien Caille
 *
 * @param <T> type of the object according to which the properties are enabled
 */
public class BindingSelector<T> implements PropertyChangeListener {

	private final Map<T, List<IBindingController>> objectControllers = new WeakHashMap<>();

	public BindingSelector(final AbstractProperty property) {
		property.addListener(this);
	}

	public void add(final T object, final IBindingController... controllers) {
		final var ctrls = objectControllers.computeIfAbsent(object, k -> new ArrayList<>());
		ctrls.addAll(List.of(controllers));
	}

	@Override
	public void propertyChange(final PropertyChangeEvent evt) {

		final var oldValue = evt.getOldValue();
		if (oldValue != null) {
			final var oldControllers = objectControllers.get(oldValue);
			if (oldControllers != null) {
				oldControllers.forEach(c -> c.getVeto().detach());
			}
		}

		final var newValue = evt.getNewValue();
		if (newValue != null) {
			final var newController = objectControllers.get(newValue);
			if (newController != null) {
				newController.forEach(c -> c.getVeto().attach());
				newController.forEach(IBindingController::forceViewUpdate);
			}
		}

	}

}
