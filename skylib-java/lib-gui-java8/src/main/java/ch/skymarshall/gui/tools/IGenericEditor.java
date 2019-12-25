package ch.skymarshall.gui.tools;

import ch.skymarshall.gui.mvc.IBindingController;
import ch.skymarshall.gui.tools.GenericEditorClassModel.PropertyEntry;

public interface IGenericEditor {

	IBindingController bind(final PropertyEntry<?> prop);

	void finish(GenericEditorAdapter<?, ?> adapter);

}
