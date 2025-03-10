package ch.scaille.javabeans;

import java.util.function.Predicate;

import ch.scaille.javabeans.Vetoer.TransmitMode;
import ch.scaille.javabeans.chain.BindingChain;

public interface IVeto {

	boolean mustSendToComponent(BindingChain chain);

	boolean mustSendToProperty(BindingChain chain);

	void attach();

	void detach();

	void inhibitTransmitToComponentWhen(Predicate<BindingChain> inhibitor);

	boolean isTransmitMode(TransmitMode both);

}
