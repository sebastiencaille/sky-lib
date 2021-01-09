package ch.skymarshall.gui.tools;

import ch.skymarshall.gui.mvc.IBindingController;
import ch.skymarshall.gui.mvc.properties.ErrorSet;

public interface IGenericEditor {

	IBindingController addEntry(final PropertyEntry prop);

	void build(GenericEditorController<?> adapter, final ErrorSet errorProperty);


}
