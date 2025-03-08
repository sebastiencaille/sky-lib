package ch.scaille.gui.tools;

import java.util.List;

import ch.scaille.javabeans.IPropertiesGroup;
import ch.scaille.javabeans.persisters.IPersisterFactory.IObjectProvider;
import ch.scaille.javabeans.properties.ErrorSet;

/**
 * A generic editor
 * @param <T>
 */
public interface IGenericEditorModel<T> {

	List<IPropertyEntry> createProperties(IObjectProvider<T> object);

	ErrorSet getErrorProperty();

	IPropertiesGroup getPropertySupport();
}
