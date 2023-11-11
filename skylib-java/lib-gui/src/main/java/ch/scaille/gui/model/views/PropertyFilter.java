package ch.scaille.gui.model.views;

import java.util.function.Predicate;

import ch.scaille.javabeans.properties.ObjectProperty;

public class PropertyFilter<D, F extends Predicate<D>> implements Predicate<D> {

	private final ObjectProperty<F> filter;

	public PropertyFilter(final ObjectProperty<F> filter) {
		this.filter = filter;
	}

	@Override
	public boolean test(final D object) {
		return filter.getValue().test(object);
	}

}
