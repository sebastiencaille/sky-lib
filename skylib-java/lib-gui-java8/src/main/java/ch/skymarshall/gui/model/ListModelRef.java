package ch.skymarshall.gui.model;

/**
 * Events are triggered by the list model implementation. This interface allows
 * to retrieve the according list model
 * 
 * @author scaille
 *
 * @param <T>
 */
public interface ListModelRef<T> {
	ListModel<T> getListModel();
}
