package ch.scaille.gui.model.views;

import java.util.function.BiPredicate;

import ch.scaille.javabeans.IComponentBinding;
import ch.scaille.javabeans.IComponentChangeSource;
import ch.scaille.javabeans.IComponentLink;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 * A list view that can be dynamically modified using a parameter provided through a component binding.
 * @param <T> the type of the sorted/filtered value
 * @param <P> the type of the view's parameter
 */
@NullMarked
public class DynamicListView<T extends @Nullable Object, P> extends AbstractListView<T>
        implements IComponentBinding<P> {


    public interface DynamicComparator<T extends @Nullable Object, P> {
        int compare(T o1, T o2, P parameters);
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
    public boolean test(T object) {
        if (filter != null) {
            return filter.test(object, viewParameter);
        }
        return super.test(object);
    }

    @Override
    public int compare(T o1, T o2) {
        if (comparator != null) {
            return comparator.compare(o1, o2, viewParameter);
        }
        return super.compare(o1, o2);
    }
}
