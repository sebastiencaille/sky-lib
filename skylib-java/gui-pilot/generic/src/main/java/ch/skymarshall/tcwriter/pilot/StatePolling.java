package ch.skymarshall.tcwriter.pilot;

import java.util.function.Predicate;

public class StatePolling<C, V> extends Polling<C, V> {

	public StatePolling(final PollingFunction<C, V> pollingFunction) {
		super(null, pollingFunction);
	}

	public StatePolling(final Predicate<C> precondition, final PollingFunction<C, V> pollingFunction) {
		super(precondition, pollingFunction);
	}

	@Override
	public Predicate<C> getPrecondition(final AbstractGuiComponent<?, C> guiComponent) {
		if (super.getPrecondition(guiComponent) != null) {
			return super.getPrecondition(guiComponent);
		}
		return guiComponent::canCheck;
	}

}
