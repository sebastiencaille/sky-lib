package ch.skymarshall.gui.mvc.factories;

import ch.skymarshall.gui.model.ListEvent;
import ch.skymarshall.gui.model.ListModel;
import ch.skymarshall.gui.model.ListModelAdapter;
import ch.skymarshall.gui.mvc.IBindingChainDependency;
import ch.skymarshall.gui.mvc.IBindingController;
import ch.skymarshall.gui.mvc.IPropertyEventListener;
import ch.skymarshall.gui.mvc.PropertyEvent;
import ch.skymarshall.gui.mvc.ScreenBuildingReport;
import ch.skymarshall.gui.mvc.properties.AbstractProperty;

public interface BindingDependencies {

	public static class DetachOnPropertyUpdate implements IBindingChainDependency, IPropertyEventListener {

		private final AbstractProperty property;
		private IBindingController controller;

		public DetachOnPropertyUpdate(final AbstractProperty property) {
			this.property = property;
		}

		@Override
		public void register(final IBindingController controller) {
			this.controller = controller;
			property.addListener(this);
			ScreenBuildingReport.addDependency(this, controller);
		}

		@Override
		public void unbind() {
			property.removeListener(this);
		}

		@Override
		public void propertyModified(final Object caller, final PropertyEvent event) {
			switch (event.getKind()) {
			case BEFORE:
				controller.detach();
				break;
			case AFTER:
				controller.attach();
				controller.forceViewUpdate();
				break;
			default:
				// ignore
				break;
			}
		}

		@Override
		public String toString() {
			return "Detaching on update of " + property.getName();
		}
	}

	public static IBindingChainDependency detachOnUpdateOf(final AbstractProperty property) {
		return new DetachOnPropertyUpdate(property);
	}

	/**
	 * Restores the selection once some properties are fired.
	 * <p>
	 *
	 * @param property the property that contains the selection to restore
	 * @return an action
	 */

	public static class DetachOnUpdateOfListModelUpdate<T> extends ListModelAdapter<T>
			implements IBindingChainDependency {

		private final ch.skymarshall.gui.model.ListModel<T> model;
		private IBindingController controller;

		public DetachOnUpdateOfListModelUpdate(final ListModel<T> model) {
			this.model = model;
		}

		@Override
		public void register(final IBindingController controller) {
			this.controller = controller;
			model.addListener(this);
		}

		@Override
		public void unbind() {
			model.removeListener(this);
		}

		@Override
		public void mutates() {
			controller.detach();
		}

		@Override
		public void valuesSet(final ListEvent<T> event) {
			controller.attach();
		}

		@Override
		public void valuesCleared(final ListEvent<T> event) {
			controller.attach();
		}

		@Override
		public void valuesAdded(final ListEvent<T> event) {
			controller.attach();
		}

		@Override
		public void valuesRemoved(final ListEvent<T> event) {
			controller.attach();
		}

		@Override
		public String toString() {
			return "Detaching on update of " + model;
		}

	}

	public static <T> IBindingChainDependency detachOnUpdateOf(final ListModel<T> model) {
		return new DetachOnUpdateOfListModelUpdate<>(model);
	}
}
