package ch.scaille.javabeans.chain;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import ch.scaille.javabeans.IBindingControl;
import ch.scaille.javabeans.IVetoer;
import ch.scaille.javabeans.properties.AbstractProperty;

public class Vetoer implements IVetoer {

	private int detached = 0;

	private final TransmitMode transmitMode;

	private final List<Predicate<AbstractProperty>> transmitToComponentInhibitors = new ArrayList<>();

	public Vetoer(TransmitMode startupTransmitMode) {
		transmitMode = startupTransmitMode;
	}

	@Override
	public boolean release() {
		if (detached > 0) {
			detached--;
		}
		return detached == 0;
	}

	@Override
	public void bufferize() {
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
