package org.skymarshall.hmi.model.views;

import java.util.function.Consumer;
import java.util.function.Predicate;

import org.skymarshall.hmi.mvc.IComponentBinding;
import org.skymarshall.util.Lambda;

public abstract class AbstractDynamicFilter<T> implements Predicate<T> {

	private IListViewOwner<T> viewOwner;

	public void attach(final IListViewOwner<T> aViewOwner) {
		this.viewOwner = aViewOwner;
	}

	public <U> IComponentBinding<U> filterUpdate(final Consumer<U> c) {
		return IComponentBinding.<AbstractDynamicFilter<T>, U>component(AbstractDynamicFilter.this,
				Lambda.nothingBiConsumer(), (f, p, v) -> {
					c.accept(v);
					if (viewOwner != null) {
						viewOwner.viewUpdated();
					}
				});
	}

}
