package ch.scaille.gui.model;

/**
 * Events are triggered by the list model implementation. This interface allows
 * retrieving the according list model
 * 
 * @author scaille
 *
 * @param <T>
 */
public interface ListModelRef<T> {
	ListModel<T> getListModel();
}
