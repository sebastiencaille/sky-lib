package ch.scaille.gui.tools;

import static ch.scaille.javabeans.properties.Configuration.errorNotifier;

import java.util.List;

import ch.scaille.javabeans.IPropertiesGroup;
import ch.scaille.javabeans.persisters.ObjectProviderPersister.CurrentObjectProvider;
import ch.scaille.javabeans.properties.ErrorSet;

/**
 * This controller is the root of the generic editor. It binds the view
 * and the model.
 *
 * @author scaille
 *
 */
public class GenericEditorController<T> {

	private final IGenericEditor editor;
	private final IPropertiesGroup propertySupport;
	private final ErrorSet errorProperty;
	private final List<? extends PropertyEntry> props;
	private final CurrentObjectProvider<T> currentObject = new CurrentObjectProvider<>();

	public GenericEditorController(final IGenericEditor view, final IGenericEditorModel<T> modelAdapter) {
		this.editor = view;
		this.propertySupport = modelAdapter.getPropertySupport();
		this.errorProperty = modelAdapter.getErrorProperty();
		props = modelAdapter.createProperties(currentObject);
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

	public void loadUnsafe(final Object obj) {
		load((T) obj);
	}

	public void save() {
		if (!errorProperty.getErrors().getValue().isEmpty()) {
			return;
		}
		props.forEach(PropertyEntry::saveInCurrentObject);
	}

}
