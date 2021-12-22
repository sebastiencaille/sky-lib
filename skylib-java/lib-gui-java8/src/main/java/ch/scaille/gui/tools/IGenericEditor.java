package ch.scaille.gui.tools;

import ch.scaille.gui.mvc.IBindingController;
import ch.scaille.gui.mvc.properties.ErrorSet;

public interface IGenericEditor {

	IBindingController addEntry(final PropertyEntry prop,  ErrorSet errorProperty);

	void build(GenericEditorController<?> adapter, final ErrorSet errorProperty);

}
