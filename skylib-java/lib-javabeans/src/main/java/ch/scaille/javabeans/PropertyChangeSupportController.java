package ch.scaille.javabeans;

import static java.util.Arrays.stream;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import ch.scaille.javabeans.PropertyEvent.EventKind;
import ch.scaille.javabeans.Veto.TransmitMode;
import ch.scaille.javabeans.properties.AbstractProperty;
import ch.scaille.javabeans.properties.IPropertyEventListener;

/**
 * PropertyChangeSupport adapted to the property controllers.
 * <p>
 *
 * The support can detect loops that may appear when a chain of properties are
 * fired.
 * <p>
 * By default, all the properties are detached from the controller. They can be
 * attached using the method startController
 *
 * @author Sebastien Caille
 *
 */
public class PropertyChangeSupportController {



	private static class CallInfo {

		private final Object caller;
		private final Object newValue;

		public CallInfo(final Object caller, final Object newValue) {
			this.caller = caller;
			this.newValue = newValue;
		}

		@Override
		public String toString() {
			return caller + " -> " + newValue + "\n";
		}

	}

	private final PropertyChangeSupport support;
	
	/** Information about properties currently called */
	private final Map<String, Deque<CallInfo>> callInfo = new HashMap<>();

	private final Map<Object, PropertiesGroup> scopedRegistrations = new IdentityHashMap<>();

	public static IPropertiesGroup mainGroup(Object bean) {
		return new PropertyChangeSupportController(bean).scoped(bean);
	}
	
	public PropertyChangeSupportController(final Object bean) {
		support = new PropertyChangeSupport(bean);
	}

	private void endFire(final String propertyName) {
		final var info = callInfo.get(propertyName);
		info.pop();
		if (info.isEmpty()) {
			callInfo.remove(propertyName);
		}
	}

	private void prepareFire(final String propertyName, final Object caller, final Object newValue) {
		final var info = callInfo.computeIfAbsent(propertyName, k -> new ArrayDeque<>(5));
		if (info.size() > 5) {
			final var stack = new StringBuilder();
			info.stream().forEach(i -> stack.append(i).append(";"));
			throw new IllegalStateException(propertyName + " is already fired:" + stack);
		}
		info.push(new CallInfo(caller, newValue));
	}

	public boolean isBeingFired(final String propertyName) {
		return callInfo.containsKey(propertyName);
	}

	public boolean isModifiedBy(final String name, final Object caller) {
		final var info = callInfo.get(name);
		if (info == null) {
			return false;
		}
		return info.stream().anyMatch(i -> i.caller == caller);
	}

	public String getModificationStack(String name) {
		final var info = callInfo.get(name);
		if (info == null) {
			return "";
		}
		return info.stream().map(Object::toString).collect(Collectors.joining("\n"));
	}

	public void removePropertyChangeListener(final String name, final PropertyChangeListener propertyChangeListener) {
		support.removePropertyChangeListener(name, propertyChangeListener);
	}

	public void removeAllPropertyChangeListener(final String name) {
		stream(support.getPropertyChangeListeners(name)).forEach(support::removePropertyChangeListener);
	}

	/**
	 * Triggers a property change
	 *
	 * @param propertyName name of the property
	 * @param caller       identification of the method's caller
	 * @param oldValue     the old value of the property
	 * @param newValue     the new value of the property
	 */
	public void firePropertyChange(final String propertyName, final Object caller, final Object oldValue,
			final Object newValue) {
		if (Objects.equals(oldValue, newValue)) {
			return;
		}
		prepareFire(propertyName, caller, newValue);
		try {
			support.firePropertyChange(propertyName, oldValue, newValue);
		} finally {
			endFire(propertyName);
		}
	}

	public void unregister(final AbstractProperty abstractProperty) {
		final var propName = abstractProperty.getName();
		final var propertyListeners = support.getPropertyChangeListeners(propName);
		Arrays.stream(propertyListeners).forEach(l -> support.removePropertyChangeListener(propName, l));
	}

	public void unregister(final AbstractProperty... abstractProperties) {
		Arrays.stream(abstractProperties).forEach(this::unregister);
	}

	private static class ListenerRegistration {

		private final String name;
		private final PropertyChangeListener listener;

		public ListenerRegistration(final String name, final PropertyChangeListener listener) {
			this.name = name;
			this.listener = listener;
		}
	}

	public class PropertiesGroup implements IPropertiesGroup {
		/** All the properties of the MVC */
		private final List<AbstractProperty> properties = new ArrayList<>();
		private final List<ListenerRegistration> listeners = new ArrayList<>();
		private final Object scope;

		public PropertiesGroup(final Object scope) {
			this.scope = scope;
		}

		@Override
		public PropertyChangeSupportController getChangeSupport() {
			return PropertyChangeSupportController.this;
		}

		@Override
		public void register(final AbstractProperty abstractProperty) {
			properties.add(abstractProperty);
		}

		@Override
		public void attachAll() {
			properties.forEach(AbstractProperty::attach);
		}

		@Override
		public void detachAll() {
			properties.forEach(p -> p.setTransmitMode(TransmitMode.NONE));
		}

		@Override
		public void transmitAllToComponentOnly() {
			properties.forEach(p -> p.setTransmitMode(TransmitMode.TO_COMPONENT_ONLY));
		}

		@Override
		public void enableAllTransmit() {
			properties.forEach(p -> p.setTransmitMode(TransmitMode.BOTH));
		}

		@Override
		public void addPropertyChangeListener(final String name, final PropertyChangeListener propertyChangeListener) {
			listeners.add(new ListenerRegistration(name, propertyChangeListener));
			support.addPropertyChangeListener(name, propertyChangeListener);
		}

		@Override
		public void disposeBindings() {
			properties.forEach(PropertyChangeSupportController.this::unregister);
			listeners.forEach(
					l -> PropertyChangeSupportController.this.removePropertyChangeListener(l.name, l.listener));
		}

		@Override
		public IPropertyEventListener detachWhenPropLoading() {
			return (caller, event) -> {
				if (event.getKind() == EventKind.BEFORE) {
					transmitAllToComponentOnly();
				} else if (event.getKind() == EventKind.AFTER) {
					enableAllTransmit();
				}
			};
		}

		@Override
		public String toString() {
			return "Scoped controller: " + scope;
		}
	}

	public IPropertiesGroup scoped(final Object scope) {
		return scopedRegistrations.computeIfAbsent(scope, PropertiesGroup::new);
	}

}
