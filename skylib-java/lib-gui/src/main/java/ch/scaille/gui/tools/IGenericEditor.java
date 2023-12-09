package ch.scaille.gui.tools;

import ch.scaille.javabeans.IBindingController;
import ch.scaille.javabeans.properties.ErrorSet;

/**
 * A generic property editor
 */
public interface IGenericEditor {

	IBindingController addEntry(final PropertyEntry prop, ErrorSet errorProperty);

	/**
	 * Extra operation needed to build the editor (after the entries have been added to the editor)
	 */
	void build(GenericEditorController<?> adapter, final ErrorSet errorProperty);

}
