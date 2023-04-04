package ch.scaille.gui.tools;

import java.util.List;

import ch.scaille.gui.mvc.IPropertiesGroup;
import ch.scaille.gui.mvc.persisters.ObjectProviderPersister.IObjectProvider;
import ch.scaille.gui.mvc.properties.ErrorSet;

public interface IGenericEditorModel<T> {

	List<? extends PropertyEntry> createProperties(IObjectProvider<T> object);

	ErrorSet getErrorProperty();

	IPropertiesGroup getPropertySupport();
}
