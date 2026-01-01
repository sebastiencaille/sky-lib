package ch.scaille.gui.model.views;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.io.Serial;
import java.io.Serializable;
import java.util.Comparator;
import java.util.Objects;

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
@NullMarked
public abstract class AbstractListView<T extends @Nullable Object> implements IListView<T>, Serializable {

	@Serial
	private static final long serialVersionUID = -4696996416566266010L;

	/**
	 * Used to sort elements that are equals
	 */
	@Nullable
	protected transient Comparator<? super T> parentComparator = null;

	/**
	 * Creates a list view using a comparator and a filter
	 *
     */
	protected AbstractListView() {
	}

	@Override
	public boolean accept(final T object) {
		return true;
	}

	@Override
	public int compare(final T o1, final T o2) {
		return Objects.requireNonNull(parentComparator, this + ": parent comparator is null").compare(o1, o2);
	}

	@Override
	public void attach(final IListViewOwner<T> owner) {
		this.parentComparator = owner.parentComparator();
	}

	@Override
	public void detach(final IListViewOwner<T> owner) {
		// no op
	}

}
