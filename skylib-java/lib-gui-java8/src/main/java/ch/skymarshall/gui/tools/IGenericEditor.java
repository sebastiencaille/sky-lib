package ch.skymarshall.gui.tools;

import ch.skymarshall.gui.mvc.IBindingController;
import ch.skymarshall.gui.mvc.properties.ErrorSet;
import ch.skymarshall.gui.tools.GenericEditorClassModel.PropertyEntry;

public interface IGenericEditor {

	IBindingController bind(final PropertyEntry<?> prop);

	void build(GenericEditorController<?> adapter, final ErrorSet errorProperty);

}
