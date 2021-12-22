package ch.scaille.gui.tools;

import java.util.List;

import ch.scaille.gui.mvc.IScopedSupport;
import ch.scaille.gui.mvc.persisters.ObjectProviderPersister.IObjectProvider;

public interface IGenericEditorModel<T> {

	List<? extends PropertyEntry> createProperties(IScopedSupport propertySupport, IObjectProvider<T> object);

}
