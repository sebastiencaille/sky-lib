package ch.scaille.gui.model.views;

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

	Comparator<T> parentComparator();

}
