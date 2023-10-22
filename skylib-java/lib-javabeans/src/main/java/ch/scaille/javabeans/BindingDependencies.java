package ch.scaille.javabeans;

import ch.scaille.javabeans.properties.AbstractProperty;
import ch.scaille.javabeans.properties.IPropertyEventListener;

public final class BindingDependencies {

	private BindingDependencies() {
	}

	public static class PreserveOnUpdateOf implements IBindingChainDependency, IPropertyEventListener {

		private final AbstractProperty property;
		private IBindingController controller;

		public PreserveOnUpdateOf(final AbstractProperty property) {
			this.property = property;
		}

		@Override
		public void register(final IBindingController controller) {
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
			switch (event.getKind()) {
			case BEFORE:
				if (event.getProperty().mustSendToComponent()) {
					controller.getVeto().detach();
				}
				break;
			case AFTER:
				controller.getVeto().attach();
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
