package ch.scaille.gui.model.views;

import java.io.Serial;
import java.io.Serializable;
import java.util.Comparator;
import java.util.function.Predicate;

/**
 * Default IListView implementation.
 * <p>
 * The comparator is mandatory, because log(n) access to data requires sorted
 * data.
 *
 * @author Sebastien Caille
 *
 * @param <T>
 */
public class ListView<T> implements IListView<T>, Serializable {

	@Serial
	private static final long serialVersionUID = -4696996416566266010L;

	/**
	 * Used to sort elements that are equals 
	 */
	protected final Predicate<? super T> filter;
	protected final Comparator<? super T> comparator;
	protected Comparator<? super T> parentComparator = null;

	/**
	 * Creates a list view using a comparator and a filter
	 *
     */
	public ListView(final Comparator<? super T> comparator, final Predicate<? super T> filter) {
		this.filter = filter;
		this.comparator = comparator;
	}

	@Override
	public boolean accept(final T object) {
		return filter == null || filter.test(object);
	}

	@Override
	public int compare(final T o1, final T o2) {
		final int compare;
		if (comparator == null && parentComparator == null) {
			throw new IllegalStateException(this + ": you must either set a comparator or override this method");
		} else if (comparator != null) {
			compare = comparator.compare(o1, o2);
		} else {
			compare = 0;
		}
		if (compare == 0 && parentComparator != null) {
			return parentComparator.compare(o1, o2);
		}
		return compare;
	}

	@Override
	public void attach(final IListViewOwner<T> owner) {
		this.parentComparator = owner.parentComparator();
		if (filter instanceof AbstractDynamicView) {
			((AbstractDynamicView<T>) filter).attach(owner);
		}
	}

	@Override
	public void detach(final IListViewOwner<T> owner) {
		// no op
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + ":[comparator=" + comparator + ", filter=" + filter + ']';
	}

}
