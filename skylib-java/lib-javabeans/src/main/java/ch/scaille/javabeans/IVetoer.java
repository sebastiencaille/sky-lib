package ch.scaille.javabeans;

import java.util.function.Predicate;

import ch.scaille.javabeans.properties.AbstractProperty;
import org.jspecify.annotations.NullMarked;

/**
 * Allows to block transmission
 */
@NullMarked
public interface IVetoer {

	enum TransmitMode {
		/**
		 * When detached. Properties/components are not updated
		 */
		STOPPED(false, false),

		/**
		 * Components are kept in sync with property updates, but component changes are
		 * not propagated to properties. Useful when loading new data, i.e. when the selected
		 * item is not available because component content is not loaded
		 */
		TO_COMPONENT_ONLY(true, false),

		/**
		 * When attached. Properties/component are fully kept in sync
		 */
		TRANSMIT(true, true);

		public final boolean toComponent;
		public final boolean toProperty;

		TransmitMode(final boolean toComponent, final boolean toProperty) {
			this.toComponent = toComponent;
			this.toProperty = toProperty;
		}
	}
	
	boolean resume();

	void pause();

	void inhibitTransmitToComponentWhen(Predicate<AbstractProperty> inhibitor);

	boolean isTransmitMode(TransmitMode mode);

}
