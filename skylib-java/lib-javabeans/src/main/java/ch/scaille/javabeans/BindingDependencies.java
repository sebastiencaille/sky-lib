package ch.scaille.javabeans;

import ch.scaille.javabeans.properties.AbstractProperty;
import ch.scaille.javabeans.properties.IPropertyEventListener;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

/**
 * Allows adding dependencies between properties
 */
@NullMarked
public final class BindingDependencies {

	private BindingDependencies() {
	}

	public static class PreserveOnUpdateOf implements IBindingChainDependency, IPropertyEventListener {

		private final AbstractProperty property;
		@Nullable
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
			Objects.requireNonNull(controller, "register must have been called before");
			switch (event.kind()) {
			case BEFORE:
				controller.bufferizeBinding();
				break;
			case AFTER:
				controller.releaseBinding();
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
