package ch.skymarshall.gui.tools;

import java.util.List;

import ch.skymarshall.gui.mvc.IScopedSupport;
import ch.skymarshall.gui.mvc.persisters.ObjectProviderPersister.IObjectProvider;

public interface IGenericEditorModel<T> {

	List<? extends PropertyEntry> createProperties(IScopedSupport propertySupport, IObjectProvider<T> object);

}
