package ch.scaille.gui.model.views;

import java.util.function.BiPredicate;
import java.util.function.Consumer;

import ch.scaille.javabeans.IComponentBinding;
import ch.scaille.javabeans.IComponentChangeSource;
import ch.scaille.javabeans.IComponentLink;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 *
 * @param <T> value type
 * @param <P> view parameter type
 */
@NullMarked
public class DynamicListView<T extends @Nullable Object, P> extends AbstractListView<T>
        implements IComponentBinding<P> {


    public interface DynamicComparator<T extends @Nullable Object, P> {
        int compare(T o1, T o2, P parameters);
    }

    public interface View<T extends @Nullable Object> {
        default int compare(T o1, T o2) {
            return 0;
        }

        default boolean test(T o) {
            return true;
        }
    }


    @Nullable
    private IListViewOwner<T> viewOwner;

    private P viewParameter;

    @Nullable
    private final DynamicComparator<T, P> comparator;

    @Nullable
    private final BiPredicate<T, P> filter;

    public DynamicListView(P initialViewParameter, @Nullable DynamicComparator<T, P> comparator, @Nullable BiPredicate<T, P> filter) {
        this.comparator = comparator;
        this.filter = filter;
        this.viewParameter = initialViewParameter;
    }

    @Override
    public void attach(final IListViewOwner<T> aViewOwner) {
        super.attach(aViewOwner);
        this.viewOwner = aViewOwner;
    }

    @Override
    public void detach(final IListViewOwner<T> aViewOwner) {
        if (viewOwner == aViewOwner) {
            this.viewOwner = null;
        }
        super.detach(aViewOwner);
    }

    /**
     * Returns a component binding that calls c with the new value and refreshes the
     * view
     */
    public <U> Consumer<U> refreshAfterExecution(final Consumer<U> c) {
        return (propertyValue -> {
            c.accept(propertyValue);
            updateView();
        });
    }

    protected void updateView() {
        if (viewOwner != null) {
            viewOwner.viewUpdated();
        }
    }

    @Override
    public void setComponentValue(IComponentChangeSource source, P value) {
        this.viewParameter = value;
        updateView();
    }

    @Override
    public void addComponentValueChangeListener(IComponentLink<P> link) {
        // noop
    }

    @Override
    public void removeComponentValueChangeListener() {
        // noop
    }

    @Override
    public boolean accept(T object) {
        if (filter != null) {
            return filter.test(object, viewParameter);
        }
        return super.accept(object);
    }

    @Override
    public int compare(T o1, T o2) {
        if (comparator != null) {
            return comparator.compare(o1, o2, viewParameter);
        }
        return super.compare(o1, o2);
    }
}
