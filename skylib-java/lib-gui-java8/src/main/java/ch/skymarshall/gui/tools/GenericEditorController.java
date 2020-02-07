package ch.skymarshall.gui.tools;

import java.util.List;

import ch.skymarshall.gui.mvc.ControllerPropertyChangeSupport;
import ch.skymarshall.gui.mvc.IScopedSupport;
import ch.skymarshall.gui.mvc.properties.ErrorSet;
import ch.skymarshall.gui.tools.GenericEditorClassModel.PropertyEntry;

/**
 * This controller is the root of the generic editor. It helps binding the view
 * and the model.
 *
 * @author scaille
 *
 */
public class GenericEditorController<T> {

	private final IGenericEditor editor;
	private final IScopedSupport propertySupport = new ControllerPropertyChangeSupport(this).scoped(this);
	private final ErrorSet errorProperty = new ErrorSet("Error", propertySupport);
	private final List<PropertyEntry<T>> props;

	public GenericEditorController(final IGenericEditor view, final GenericEditorClassModel<T> modelAdapter) {
		this.editor = view;
		props = modelAdapter.createProperties(propertySupport, errorProperty);
	}

	public void activate() {
		props.forEach(editor::bind);
		editor.build(this, errorProperty);
		propertySupport.attachAll();
	}

	public void load(final T obj) {
		props.forEach(p -> p.loadFromObject(this, obj));
	}

	public void save() {
		if (!errorProperty.getErrors().getValue().isEmpty()) {
			return;
		}
		props.forEach(PropertyEntry::saveInCurrentObject);
	}

}
