package ch.scaille.gui.model.views;

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
public interface IListViewOwner<T> {

	void viewUpdated();

	@Nullable Comparator<T> parentComparator();

}
