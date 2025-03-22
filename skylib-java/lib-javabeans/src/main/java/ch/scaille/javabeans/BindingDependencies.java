package ch.scaille.javabeans;

import ch.scaille.javabeans.properties.AbstractProperty;
import ch.scaille.javabeans.properties.IPropertyEventListener;

/**
 * Allows to add dependencies between properties
 */
public final class BindingDependencies {

	private BindingDependencies() {
	}

	public static class PreserveOnUpdateOf implements IBindingChainDependency, IPropertyEventListener {

		private final AbstractProperty property;
		private IBindingControl controller;

		public PreserveOnUpdateOf(final AbstractProperty property) {
			this.property = property;
		}

		@Override
		public void register(final IBindingControl controller) {
			this.controller = controller;
			property.addListener(this);
			DependenciesBuildingReport.addDependency(this, controller);
		}

		@Override
		public void unbind() {
			property.removeListener(this);
		}

		@Override
		public void propertyModified(final Object caller, final PropertyEvent event) {
			switch (event.kind()) {
			case BEFORE:
				if (event.property().mustSendToComponent()) {
					controller.getVetoer().detach();
				}
				break;
			case AFTER:
				controller.getVetoer().attach();
				controller.forceViewUpdate();
				break;
			default:
				// ignore
				break;
			}
		}

		@Override
		public String toString() {
			return "Preserve on update of " + property.getName();
		}
	}

	public static IBindingChainDependency preserveOnUpdateOf(final AbstractProperty property) {
		return new PreserveOnUpdateOf(property);
	}

}
