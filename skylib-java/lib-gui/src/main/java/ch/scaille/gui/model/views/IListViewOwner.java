package ch.scaille.gui.model.views;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Comparator;

/**
 * Must be implemented by the ListView's owner.
 * <p>
 * 
 * @author Sebastien Caille
 * 
 * @param <T>
 */
@NullMarked
public interface IListViewOwner<T extends @Nullable Object> {

	void viewUpdated();

	@Nullable Comparator<T> parentComparator();

}
