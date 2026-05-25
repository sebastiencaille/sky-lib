package ch.scaille.gui.model;

import org.jspecify.annotations.Nullable;

/**
 * Events are triggered by the list model implementation. This interface allows
 * retrieving the according list model
 * 
 * @author scaille
 *
 * @param <T>
 */

public interface ListModelRef<T extends @Nullable Object> {
	ListModel<T> getListModel();
}
