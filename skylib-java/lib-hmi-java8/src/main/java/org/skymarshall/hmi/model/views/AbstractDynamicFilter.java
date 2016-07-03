package org.skymarshall.hmi.model.views;

import java.util.function.Predicate;

import org.skymarshall.hmi.mvc.IComponentBinding;
import org.skymarshall.hmi.mvc.IComponentLink;
import org.skymarshall.hmi.mvc.properties.AbstractProperty;

public abstract class AbstractDynamicFilter<T> implements Predicate<T> {

	private IListViewOwner<T> viewOwner;

	public void attach(final IListViewOwner<T> aViewOwner) {
		this.viewOwner = aViewOwner;
	}

	protected class FilterUpdateBinding<U> implements IComponentBinding<U> {

		@Override
		public Object getComponent() {
			return AbstractDynamicFilter.this;
		}

		@Override
		public void addComponentValueChangeListener(final IComponentLink<U> link) {
			// read-only
		}

		@Override
		public void setComponentValue(final AbstractProperty source,
				final U value) {
			if (viewOwner != null) {
				viewOwner.viewUpdated();
			}

		}
	}

}
