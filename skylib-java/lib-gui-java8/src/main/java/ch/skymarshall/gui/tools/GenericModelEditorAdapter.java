package ch.skymarshall.gui.tools;

import java.util.List;

import ch.skymarshall.gui.tools.ClassAdapter.PropertyEntry;

/**
 *
 * @author scaille
 *
 * @param <C> base of components
 */
public class GenericModelEditorAdapter<T, C> {

	private final IGenericEditor editor;
	private final List<PropertyEntry<T>> props;

	public GenericModelEditorAdapter(final IGenericEditor editor, final ClassAdapter<T> modelAdapter) {
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
