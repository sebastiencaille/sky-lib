package org.skymarshall.hmi.mvc;

import org.skymarshall.hmi.model.ListEvent;
import org.skymarshall.hmi.model.ListModel;
import org.skymarshall.hmi.model.ListModelAdapter;
import org.skymarshall.hmi.mvc.properties.AbstractProperty;

public interface ModelBindings {

	public static class DetachOnPropertyUpdate implements IBindingChainDependency, IPropertyEventListener {

		private final AbstractProperty property;
		private BindingChain chain;

		public DetachOnPropertyUpdate(final AbstractProperty property) {
			this.property = property;
		}

		@Override
		public void register(final BindingChain chain) {
			this.chain = chain;
			property.addListener(this);
		}

		@Override
		public void unbind() {
			property.removeListener(this);
		}

		@Override
		public void propertyModified(final Object caller, final PropertyEvent event) {
			switch (event.getKind()) {
			case BEFORE:
				chain.detach();
				break;
			case AFTER:
				chain.attach();
				break;
			default:
				// ignore
				break;
			}
		}

	}

	public static IBindingChainDependency detachOnUpdateOf(final AbstractProperty property) {
		return new DetachOnPropertyUpdate(property);
	}

	/**
	 * Restores the selection once some properties are fired.
	 * <p>
	 *
	 * @param property
	 *            the property that contains the selection to restore
	 * @return an action
	 */

	public static class DetachOnUpdateOfListModelUpdate<T> extends ListModelAdapter<T>
			implements IBindingChainDependency {

		private final org.skymarshall.hmi.model.ListModel<T> model;
		private BindingChain chain;

		public DetachOnUpdateOfListModelUpdate(final ListModel<T> model) {
			this.model = model;
		}

		@Override
		public void register(final BindingChain chain) {
			this.chain = chain;
			model.addListener(this);
		}

		@Override
		public void unbind() {
			model.removeListener(this);
		}

		@Override
		public void mutates() {
			chain.detach();
		}

		@Override
		public void valuesSet(final ListEvent<T> event) {
			chain.attach();
		}

		@Override
		public void valuesCleared(final ListEvent<T> event) {
			chain.attach();
		}

		@Override
		public void valuesAdded(final ListEvent<T> event) {
			chain.attach();
		}

		@Override
		public void valuesRemoved(final ListEvent<T> event) {
			chain.attach();
		}

	}

	public static <T> IBindingChainDependency detachOnUpdateOf(final ListModel<T> model) {
		return new DetachOnUpdateOfListModelUpdate<>(model);
	}
}
