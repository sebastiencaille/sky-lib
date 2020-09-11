package ch.skymarshall.gui.mvc;

import java.util.function.Predicate;

public interface IVeto {

	boolean mustSendToComponent(BindingChain chain);

	boolean mustSendToProperty(BindingChain chain);

	boolean attach();

	void detach();

	void addPropertyInhibitor(Predicate<BindingChain> inhibitor);

}
