package ch.scaille.gui.model.views;

import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

import ch.scaille.javabeans.IComponentBinding;
import ch.scaille.javabeans.IComponentChangeSource;
import ch.scaille.javabeans.IComponentLink;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 * Filter that can be used as a component Binding
 *
 * @param <D> the type of the filtered data
 * @param <F> the type of the property that filters the data
 */
@NullMarked
public abstract class BoundFilter<D, F> extends AbstractDynamicView<D> implements IComponentBinding<F>, Predicate<D> {

	private @Nullable F filterPropertyValue;
	private @Nullable IListViewOwner<D> owner;

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

	@Override
	public void attach(final IListViewOwner<D> viewOwner) {
		this.owner = viewOwner;
	}

	@Override
	public void addComponentValueChangeListener(final IComponentLink<F> link) {
		// Read-only
	}

	@Override
	public void removeComponentValueChangeListener() {
		// Read-only
	}

	@Override
	public void setComponentValue(final IComponentChangeSource source, final F value) {
		filterPropertyValue = value;
		Objects.requireNonNull(owner, "attach was not called yet").viewUpdated();
	}

	@Override
	public boolean test(final D value) {
		if (filterPropertyValue == null) {
			return true;
		}
		return accept(value, filterPropertyValue);
	}

}
