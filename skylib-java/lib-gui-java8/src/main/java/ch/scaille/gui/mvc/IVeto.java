package ch.scaille.gui.mvc;

import java.util.function.Predicate;

import ch.scaille.gui.mvc.Veto.TransmitMode;

public interface IVeto {

	boolean mustSendToComponent(BindingChain chain);

	boolean mustSendToProperty(BindingChain chain);

	boolean attach();

	void detach();

	void inhibitTransmitToComponentWhen(Predicate<BindingChain> inhibitor);

	boolean isTransmitMode(TransmitMode both);

}
