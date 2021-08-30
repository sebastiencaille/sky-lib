package ch.skymarshall.gui.tools;

import static ch.skymarshall.gui.mvc.properties.Configuration.errorNotifier;

import java.util.List;

import ch.skymarshall.gui.mvc.ControllerPropertyChangeSupport;
import ch.skymarshall.gui.mvc.IScopedSupport;
import ch.skymarshall.gui.mvc.persisters.ObjectProviderPersister.CurrentObjectProvider;
import ch.skymarshall.gui.mvc.properties.ErrorSet;

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
	private final List<? extends PropertyEntry> props;
	private final CurrentObjectProvider<T> currentObject = new CurrentObjectProvider<>();

	public GenericEditorController(final IGenericEditor view, final IGenericEditorModel<T> modelAdapter) {
		this.editor = view;
		props = modelAdapter.createProperties(propertySupport, currentObject);
		props.forEach(p -> p.getProperty().configureTyped(errorNotifier(errorProperty)));
	}

	public void activate() {
		props.forEach(p -> editor.addEntry(p, errorProperty));
		editor.build(this, errorProperty);
		propertySupport.attachAll();
	}

	public void load(final T obj) {
		currentObject.setObject(obj);
		props.forEach(p -> p.loadFromCurrentObject(this));
	}

	public void save() {
		if (!errorProperty.getErrors().getValue().isEmpty()) {
			return;
		}
		props.forEach(PropertyEntry::saveInCurrentObject);
	}

}
