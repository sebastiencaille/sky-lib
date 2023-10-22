package ch.scaille.gui.tools;

import ch.scaille.javabeans.IBindingController;
import ch.scaille.javabeans.properties.ErrorSet;

public interface IGenericEditor {

	IBindingController addEntry(final PropertyEntry prop, ErrorSet errorProperty);

	void build(GenericEditorController<?> adapter, final ErrorSet errorProperty);

}
