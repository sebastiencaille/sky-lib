package ch.scaille.javabeans;

import java.util.function.Predicate;

import ch.scaille.javabeans.chain.Vetoer.TransmitMode;
import ch.scaille.javabeans.properties.AbstractProperty;

/**
 * Allows to block transmition
 */
public interface IVetoer {

	void attach();

	void detach();

	void inhibitTransmitToComponentWhen(Predicate<AbstractProperty> inhibitor);

	boolean isTransmitMode(TransmitMode mode);

}
