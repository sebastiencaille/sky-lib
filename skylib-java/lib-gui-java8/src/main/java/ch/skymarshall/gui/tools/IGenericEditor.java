package ch.skymarshall.gui.tools;

import ch.skymarshall.gui.mvc.IBindingController;
import ch.skymarshall.gui.tools.ClassAdapter.PropertyEntry;

public interface IGenericEditor {

	IBindingController bind(final PropertyEntry<?> prop);

	void finish(GenericModelEditorAdapter<?, ?> adapter);

}
