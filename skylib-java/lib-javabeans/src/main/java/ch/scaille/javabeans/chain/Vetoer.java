package ch.scaille.javabeans.chain;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import ch.scaille.javabeans.IBindingControl;
import ch.scaille.javabeans.IVetoer;
import ch.scaille.javabeans.properties.AbstractProperty;

public class Vetoer implements IVetoer {

	public enum TransmitMode {
		/**
		 * When detached. Properties/components are not updated
		 */
		NONE(false, false),

		/**
		 * Component are kept in sync with property updates, but component changes are
		 * not propagated to properties. Useful when loading new data, i.e. when selected
		 * item is not available because component content is not loaded
		 */
		TO_COMPONENT_ONLY(true, false),

		/**
		 * When attached. Properties/component are fully kept in sync
		 */
		BOTH(true, true);

		public final boolean toComponent;
		public final boolean toProperty;

		TransmitMode(final boolean toComponent, final boolean toProperty) {
			this.toComponent = toComponent;
			this.toProperty = toProperty;
		}
	}

	private int detached = 0;

	private final TransmitMode transmitMode;

	private final List<Predicate<AbstractProperty>> transmitToComponentInhibitors = new ArrayList<>();

	public Vetoer(TransmitMode startupTransmitMode) {
		transmitMode = startupTransmitMode;
	}

	@Override
	public void attach() {
		if (detached > 0) {
			detached--;
		}
	}

	@Override
	public void detach() {
		detached++;
	}

	public boolean mustSendToComponent(final IBindingControl chain) {
		return detached == 0 && transmitMode.toComponent && chain.getProperty().mustSendToComponent()
				&& transmitToComponentInhibitors.stream().noneMatch(t -> t.test(chain.getProperty()));
	}

	public boolean mustSendToProperty(final IBindingControl chain) {
		return detached == 0 && transmitMode.toProperty && chain.getProperty().mustSendToProperty();
	}

	@Override
	public void inhibitTransmitToComponentWhen(Predicate<AbstractProperty> inhibitor) {
		transmitToComponentInhibitors.add(inhibitor);
	}

	@Override
	public boolean isTransmitMode(TransmitMode mode) {
		return mode == transmitMode;
	}
}
