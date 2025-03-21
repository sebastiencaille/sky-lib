package ch.scaille.gui.tools;

import ch.scaille.javabeans.IBindingController;

/**
 * A generic property editor
 */
public interface IGenericEditorView {

	IBindingController addEntry(final IPropertyEntry prop);

	/**
	 * Extra operation needed to build the editor (after the entries have been added to the editor)
	 */
	void build();

}
