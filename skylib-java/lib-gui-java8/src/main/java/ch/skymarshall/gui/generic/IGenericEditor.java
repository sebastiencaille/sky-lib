package ch.skymarshall.gui.generic;

import ch.skymarshall.gui.generic.ClassAdapter.PropertyEntry;
import ch.skymarshall.gui.mvc.IBindingController;

public interface IGenericEditor {

	IBindingController bind(final PropertyEntry<?> prop);

	void finish(GenericModelEditorAdapter<?, ?> adapter);

}
