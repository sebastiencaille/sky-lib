package ch.scaille.gui.model.views;

import java.util.function.Consumer;

import ch.scaille.gui.mvc.factories.ComponentBindings;
import ch.scaille.javabeans.IComponentBinding;

public abstract class AbstractDynamicView<T> {

	private IListViewOwner<T> viewOwner;

	public void attach(final IListViewOwner<T> aViewOwner) {
		this.viewOwner = aViewOwner;
	}

	public void detach(final IListViewOwner<T> aViewOwner) {
		if (viewOwner == aViewOwner) {
			this.viewOwner = null;
		}
	}

	/**
	 * Returns a component binding that calls c with the new value and refreshes the
	 * view
	 */
	public <U> IComponentBinding<U> refreshWhenUpdated(final Consumer<U> c) {
		return ComponentBindings.listen((s, v) -> {
			c.accept(v);
			if (viewOwner != null) {
				viewOwner.viewUpdated();
			}
		});
	}

}
