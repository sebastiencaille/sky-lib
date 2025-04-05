package ch.scaille.javabeans;

import java.util.function.Predicate;

import ch.scaille.javabeans.properties.AbstractProperty;

/**
 * Allows to block transmission
 */
public interface IVetoer {

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
	
	void attach();

	void detach();

	void inhibitTransmitToComponentWhen(Predicate<AbstractProperty> inhibitor);

	boolean isTransmitMode(TransmitMode mode);

}
