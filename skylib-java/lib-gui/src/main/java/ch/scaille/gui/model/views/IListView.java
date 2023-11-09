package ch.scaille.gui.model.views;

import java.util.Comparator;

/**
 * To customize the view on the list (filtering and sorting).
 * <p>
 *
 * @author Sebastien Caille
 *
 * @param <T> the type of the element to sort/filter
 */
public interface IListView<T> extends Comparator<T> {

	boolean accept(final T object);

	void attach(final IListViewOwner<T> owner);

	void detach(final IListViewOwner<T> owner);
}
