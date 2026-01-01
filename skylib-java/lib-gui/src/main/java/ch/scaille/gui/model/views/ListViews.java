package ch.scaille.gui.model.views;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Comparator;
import java.util.function.Predicate;

@NullMarked
public interface ListViews {

    static <U extends @Nullable Object> IListView<U> sortedFiltered(@Nullable final Comparator<? super U> comparator, @Nullable final Predicate<? super U> filter) {
        return new StaticListView<>(comparator, filter);
    }

    static <U extends @Nullable Object> IListView<U> sorted(final Comparator<? super U> comparator) {
        return sortedFiltered(comparator, null);
    }

    static <U extends @Nullable Object> IListView<U> filtered(final Predicate<? super U> filter) {
        return sortedFiltered(null, filter);
    }

    static <U extends Comparable<? super U>> IListView<U> sorted() {
        return sorted(Comparator.naturalOrder());
    }

    static <U extends @Nullable Object> IListView<U> inherited() {
        return new StaticListView<>(null, null);
    }

    static <U> IListView<U> simple() {
        return new StaticListView<>(Comparator.comparingLong(Object::hashCode), null);
    }

    static <T extends @Nullable Object, P extends DynamicListView.View<T>> DynamicListView<T, P> dynamic(P parameters) {
        return new DynamicListView<>(parameters,(o1, o2, p) -> p.compare(o1, o2), (o, p) -> p.test(o));
    }
}
