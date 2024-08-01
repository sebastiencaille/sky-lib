package ch.scaille.gui.tools;

import ch.scaille.javabeans.IBindingController;
import ch.scaille.javabeans.properties.ErrorSet;

/**
 * A generic property editor
 */
public interface IGenericEditor<T> {

	IBindingController addEntry(final IPropertyEntry<T> prop, ErrorSet errorProperty);

	/**
	 * Extra operation needed to build the editor (after the entries have been added to the editor)
	 */
	void build(GenericEditorController<T> adapter, final ErrorSet errorProperty);

}
