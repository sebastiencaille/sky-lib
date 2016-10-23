package org.skymarshall.hmi.mvc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.skymarshall.hmi.mvc.properties.AbstractProperty;

public class DependenciesManager {

	private final Map<AbstractProperty, List<IPropertyEventListener>> listenersPerObject = new HashMap<>();
	private final Set<AbstractProperty> parents = new HashSet<>();

	public void dependOn(final AbstractProperty child, final AbstractProperty parent) {
		final IPropertyEventListener listener = Actions.restoreAfterUpdate(child);

		List<IPropertyEventListener> currentListeners = listenersPerObject.get(child);
		if (currentListeners == null) {
			currentListeners = new ArrayList<>();
			listenersPerObject.put(child, currentListeners);
		}

		currentListeners.add(listener);
		parent.addListener(listener);
		parents.add(parent);
	}

	public void remove(final AbstractProperty child) {
		final List<IPropertyEventListener> toRemove = listenersPerObject.remove(child);
		if (toRemove == null) {
			return;
		}
		for (final AbstractProperty parent : parents) {
			parent.removeListeners(toRemove);
		}
	}

}
