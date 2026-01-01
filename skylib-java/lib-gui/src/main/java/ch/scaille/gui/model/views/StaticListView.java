package ch.scaille.gui.model.views;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.io.Serial;
import java.util.Comparator;
import java.util.function.Predicate;

/**
 * Default IListView implementation.
 * <p>
 * The comparator is mandatory because log(n) access to data requires sorted data.
 *
 * @param <T>
 */
@NullMarked
public class StaticListView<T> extends AbstractListView<T> {

    @Serial
    private static final long serialVersionUID = -4696996416566266010L;

    /**
     * Used to sort elements that are equals
     */
    @Nullable
    protected final Predicate<? super T> filter;
    @Nullable
    protected final Comparator<? super T> comparator;

    /**
     * Creates a list view using a comparator and a filter
     *
     */
    public StaticListView(@Nullable final Comparator<? super T> comparator, @Nullable final Predicate<? super T> filter) {
        this.filter = filter;
        this.comparator = comparator;
    }

    @Override
    public boolean accept(final T object) {
        return filter == null || filter.test(object);
    }

    @Override
    public int compare(final T o1, final T o2) {
        if (comparator != null) {
            return comparator.compare(o1, o2);
        }
        return parentComparator.compare(o1, o2);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + ":[comparator=" + comparator + ", filter=" + filter + ']';
    }

}
