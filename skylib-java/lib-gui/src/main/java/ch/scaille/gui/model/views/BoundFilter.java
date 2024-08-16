package ch.scaille.gui.model.views;

import java.util.function.BiPredicate;
import java.util.function.Predicate;

import ch.scaille.javabeans.IComponentBinding;
import ch.scaille.javabeans.IComponentLink;
import ch.scaille.javabeans.properties.AbstractProperty;

/**
 * Filter that can be used as a component Binding
 *
 * @author scaille
 *
 * @param <D> the type of the filtered data
 * @param <F> the type of the property that filters the data
 */
public abstract class BoundFilter<D, F> extends AbstractDynamicView<D> implements IComponentBinding<F>, Predicate<D> {

	private F filterPropertyValue;
	private IListViewOwner<D> owner;

	protected abstract boolean accept(D value, F filter);

	/**
	 * * @param <D> the type of the filtered data
	 * 
	 * @param <F>      the type of the property that filters the data
     */
	public static <D, F> BoundFilter<D, F> filter(final BiPredicate<D, F> consumer) {
		return new BoundFilter<>() {

			@Override
			protected boolean accept(final D value, final F filter) {
				return consumer.test(value, filter);
			}
		};
	}

	protected BoundFilter() {
	}

	protected F getFilterPropertyValue() {
		return filterPropertyValue;
	}

	@Override
	public void attach(final IListViewOwner<D> viewOwner) {
		this.owner = viewOwner;
	}

	@Override
	public void addComponentValueChangeListener(final IComponentLink<F> link) {
		// Read only
	}

	@Override
	public void removeComponentValueChangeListener() {
		// Read only
	}

	@Override
	public void setComponentValue(final AbstractProperty source, final F value) {
		filterPropertyValue = value;
		owner.viewUpdated();
	}

	@Override
	public boolean test(final D value) {
		if (filterPropertyValue == null) {
			// not yet initialized
			return false;
		}
		return accept(value, filterPropertyValue);
	}

}
