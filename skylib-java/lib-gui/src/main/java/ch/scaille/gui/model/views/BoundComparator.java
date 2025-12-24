package ch.scaille.gui.model.views;

import java.util.Comparator;
import java.util.Objects;

import ch.scaille.javabeans.IComponentBinding;
import ch.scaille.javabeans.IComponentChangeSource;
import ch.scaille.javabeans.IComponentLink;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 * View with a comparator that can be used as a component Binding
 *
 * @param <D> type of the compared data
 * @param <C> type of the comparator
 */
@NullMarked
public abstract class BoundComparator<D, C> extends AbstractDynamicView<D>
		implements IComponentBinding<C>, Comparator<D> {

	private C sorterPropertyValue;
	private @Nullable IListViewOwner<D> owner;

	protected abstract int compare(D value1, D value2, C filter);

	/**
	 * @param <D> Type of the sorted data
	 * @param <C> Type of the comparator property
	 */
	@FunctionalInterface
	public interface BoundComparatorFunc<D, C> {
		int compare(D data1, D data2, C propertyValue);
	}

	/**
	 * @param <D>        Type of the sorted data
	 * @param <C>        Type of the comparator property
     */
	public static <D, C> BoundComparator<D, C> comparator(final BoundComparatorFunc<D, C> comparator) {
		return new BoundComparator<>() {
			@Override
			protected int compare(final D value1, final D value2, final C filter) {
				return comparator.compare(value1, value2, filter);
			}
		};
	}

	protected BoundComparator() {
	}

	protected C getSorterPropertyValue() {
		return sorterPropertyValue;
	}

	@Override
	public void attach(final IListViewOwner<D> viewOwner) {
		this.owner = viewOwner;
	}

	@Override
	public void addComponentValueChangeListener(final IComponentLink<C> link) {
		// Read only
	}

	@Override
	public void removeComponentValueChangeListener() {
		// Read-only
	}

	@Override
	public void setComponentValue(final IComponentChangeSource source, final C value) {
		sorterPropertyValue = value;
		Objects.requireNonNull(owner, "attach was not called yet").viewUpdated();
	}

	@Override
	public int compare(final D value1, final D value2) {
		return compare(value1, value2, sorterPropertyValue);
	}

}
