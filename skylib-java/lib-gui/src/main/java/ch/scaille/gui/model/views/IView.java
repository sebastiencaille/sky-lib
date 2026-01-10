package ch.scaille.gui.model.views;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Comparator;

@NullMarked
public interface IView<T extends @Nullable Object> extends Comparator<T> {

    default int compare(T o1, T o2) {
        return 0;
    }

    default boolean test(T o) {
        return true;
    }
}
