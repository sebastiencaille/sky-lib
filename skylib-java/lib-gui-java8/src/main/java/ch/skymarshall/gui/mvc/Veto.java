package ch.skymarshall.gui.mvc;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class Veto implements IVeto {

	public enum TransmitMode {
		/**
		 * When detached. Properties/components are not updated
		 */
		NONE(false, false),

		/**
		 * Component are kept in sync with property updates, but component changes are
		 * not propagated to properties. Useful when loading new data, eg when selected
		 * item is not available because component content is not loaded
		 */
		TO_COMPONENT_ONLY(true, false),

		/**
		 * When attached. Properties/component are kept in sync
		 */
		BOTH(true, true);

		public final boolean toComponent;
		public final boolean toProperty;

		private TransmitMode(final boolean toComponent, final boolean toProperty) {
			this.toComponent = toComponent;
			this.toProperty = toProperty;
		}
	}

	private int detached = 0;

	private TransmitMode transmitMode;

	private List<Predicate<BindingChain>> transmitToComponentInhibitors = new ArrayList<>();

	public Veto(TransmitMode startupTransmitMode) {
		transmitMode = startupTransmitMode;
	}

	@Override
	public boolean attach() {
		if (detached > 0) {
			detached--;
		}
		return detached == 0;
	}

	@Override
	public void detach() {
		detached++;
	}

	@Override
	public boolean mustSendToComponent(final BindingChain chain) {
		return detached == 0 && transmitMode.toComponent && chain.getProperty().mustSendToComponent()
				&& transmitToComponentInhibitors.stream().noneMatch(t -> t.test(chain));
	}

	@Override
	public boolean mustSendToProperty(final BindingChain chain) {
		return detached == 0 && transmitMode.toProperty && chain.getProperty().mustSendToProperty();
	}

	@Override
	public void inhibitTransmitToComponentWhen(Predicate<BindingChain> inhibitor) {
		transmitToComponentInhibitors.add(inhibitor);
	}

	@Override
	public boolean isTransmitMode(TransmitMode mode) {
		return mode == transmitMode;
	}
}
