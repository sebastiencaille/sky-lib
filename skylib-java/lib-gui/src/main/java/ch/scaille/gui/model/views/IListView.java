package ch.scaille.gui.model.views;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 * To customize the view on the list (filtering and sorting).
 * <p>
 *
 * @author Sebastien Caille
 *
 * @param <T> the type of the element to sort/filter
 */
@NullMarked
public interface IListView<T extends @Nullable Object> extends IView<T> {

	void attach(final IListViewOwner<T> owner);

	void detach(final IListViewOwner<T> owner);
}
