package ch.scaille.gui.tools;

import static ch.scaille.javabeans.properties.Configuration.errorNotifier;

import java.util.List;
import java.util.function.Function;

import ch.scaille.javabeans.IPropertiesGroup;
import ch.scaille.javabeans.persisters.IPersisterFactory.ObjectHolder;
import ch.scaille.javabeans.properties.ErrorSet;

/**
 * This controller is the root of the generic editor. It binds the view
 * and the model.
 *
 * @author scaille
 *
 */
public class GenericEditorController<T> {

	private final IPropertiesGroup propertySupport;
	private final ErrorSet errorProperty;
	private final List<IPropertyEntry> props;
	private final ObjectHolder<T> currentObject = new ObjectHolder<>();

	public GenericEditorController(final IGenericEditorModel<T> modelAdapter) {
		this.propertySupport = modelAdapter.getPropertySupport();
		this.errorProperty = modelAdapter.getErrorProperty();
		props = modelAdapter.createProperties(currentObject);
		props.forEach(p -> p.getProperty().configure(errorNotifier(errorProperty)));
	}

	public ErrorSet getErrorProperty() {
		return errorProperty;
	}
	
	public <V extends IGenericEditorView> V build(Function<GenericEditorController<T>, V> viewFactory) {
		final var view = viewFactory.apply(this);
		props.forEach(view::addEntry);
		view.build();
		propertySupport.attachAll();
		return view;
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
		props.forEach(IPropertyEntry::saveInCurrentObject);
	}

}
