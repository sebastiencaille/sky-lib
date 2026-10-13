package ch.scaille.gui.model.views;

import java.util.Comparator;


public interface IView<T> extends Comparator<T> {

    default int compare(T o1, T o2) {
        return 0;
    }

    default boolean test(T o) {
        return true;
    }
}
