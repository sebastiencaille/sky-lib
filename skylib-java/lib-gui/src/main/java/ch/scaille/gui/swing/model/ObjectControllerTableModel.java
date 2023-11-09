package ch.scaille.gui.swing.model;

import static java.util.stream.Collectors.toSet;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import ch.scaille.gui.model.ListModel;
import ch.scaille.gui.mvc.IObjectGuiModel;
import ch.scaille.javabeans.IComponentBinding;
import ch.scaille.javabeans.IComponentLink;
import ch.scaille.javabeans.properties.AbstractProperty;

/**
 * A table model that is using an object controller per column.
 * <p>
 *
 * @author Sebastien Caille
 *
 * @param <O> type of the displayed object
 * @param <M> type of the object's gui model
 * @param <C> type that enums the columns
 */
@SuppressWarnings("serial")
public abstract class ObjectControllerTableModel<O, M extends IObjectGuiModel<O>, C extends Enum<C>>
		extends ListModelTableModel<O, C> {

	/**
	 *
	 * @author scaille
	 *
	 * @param <O> type of the displayed object
	 * @param <U> type of the column's data
	 */
	static class TableBinding<O, U> implements IComponentBinding<U> {

		private final Map<O, U> changes = new HashMap<>();

		private AbstractProperty property;

		private IComponentLink<U> singleListener;

		private Object loadedValue;

		@SuppressWarnings("unchecked")
		void addChange(final O object, final Object newValue) {
			changes.put(object, (U) newValue);
		}

		void commit(final Object object) {
			if (changes.containsKey(object)) {
				singleListener.setValueFromComponent(null, changes.get(object));
				property.save();
			}
		}

		Object getDisplayValue(final Object object) {
			if (changes.containsKey(object)) {
				return changes.get(object);
			}
			// This calls setComponentValue(...)
			property.load(this);
			return loadedValue;
		}

		@Override
		public void addComponentValueChangeListener(final IComponentLink<U> converter) {
			this.singleListener = converter;
		}

		@Override
		public void removeComponentValueChangeListener() {
			this.singleListener = null;
		}

		@Override
		public void setComponentValue(final AbstractProperty source, final U value) {
			this.loadedValue = value;
		}

	}

	private final TableBinding<O, ?>[] bindings;

	private final M objectModel;

	/**
	 * Binds all model properties with this model's bindings
	 *
	 * @param aModel
	 */
	protected abstract void bindModel(M anObjectModel);

	protected abstract AbstractProperty getPropertyAt(M anObjectModel, C column);

	protected ObjectControllerTableModel(final ListModel<O> listModel, final M objectModel,
			final Class<C> columnsEnumClass) {
		super(listModel, columnsEnumClass);
		this.objectModel = objectModel;
		bindings = new TableBinding[columnsEnumClass.getEnumConstants().length];
		bindModel(objectModel);

		for (final var column : columnsEnumClass.getEnumConstants()) {
			bindings[column.ordinal()].property = getPropertyAt(objectModel, column);
		}
	}

	protected <U> IComponentBinding<U> createColumnBinding(final C column) {
		final var binding = new TableBinding<O, U>();
		bindings[column.ordinal()] = binding;
		return binding;
	}

	@Override
	protected Object getValueAtColumn(final O object, final C column) {
		final var binding = bindings[column.ordinal()];
		objectModel.setCurrentObject(object);
		return binding.getDisplayValue(object);
	}

	@Override
	protected void setValueAtColumn(final O object, final C column, final Object value) {
		bindings[column.ordinal()].addChange(object, value);
	}

	public void commit() {
		final var changes = Arrays.stream(bindings).flatMap(b -> b.changes.keySet().stream()).collect(toSet());
		for (final var change : changes) {
			objectModel.setCurrentObject(change);
			model.editValue(change, c -> Arrays.stream(bindings).forEach(b -> b.commit(c)));
		}
		Arrays.stream(bindings).forEach(b -> b.changes.clear());
	}
}
