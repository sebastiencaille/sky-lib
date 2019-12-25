package ch.skymarshall.gui.tools;

import java.util.List;

import ch.skymarshall.gui.tools.GenericEditorClassModel.PropertyEntry;

/**
 *
 * @author scaille
 *
 * @param <C> base of components
 */
public class GenericEditorAdapter<T, C> {

	private final IGenericEditor editor;
	private final List<PropertyEntry<T>> props;

	public GenericEditorAdapter(final IGenericEditor editor, final GenericEditorClassModel<T> modelAdapter) {
		this.editor = editor;
		props = modelAdapter.getProperties();
	}

	public void apply() {
		props.forEach(editor::bind);
		editor.finish(this);
	}

	public void load(final T obj) {
		props.forEach(p -> p.loadFromObject(this, obj));
	}

	public void save() {
		props.forEach(PropertyEntry::saveInCurrentObject);
	}

}
