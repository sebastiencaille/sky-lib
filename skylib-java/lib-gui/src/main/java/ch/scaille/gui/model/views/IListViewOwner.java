package ch.scaille.gui.model.views;

/**
 * Must be implemented by the ListView's owner.
 * <p>
 * 
 * @author Sebastien Caille
 * 
 * @param <T>
 */
public interface IListViewOwner<T> {

	IListView<T> getParentView();

	void viewUpdated();

}
