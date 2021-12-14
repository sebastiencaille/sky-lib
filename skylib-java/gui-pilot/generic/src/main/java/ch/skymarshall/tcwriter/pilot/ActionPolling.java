package ch.skymarshall.tcwriter.pilot;

import java.util.function.Predicate;

public class ActionPolling<C, V> extends Polling<C, V> {

	public ActionPolling(final PollingFunction<C, V> pollingFunction) {
		super(null, pollingFunction);
	}

	public ActionPolling(final Predicate<C> precondition, final PollingFunction<C, V> pollingFunction) {
		super(precondition, pollingFunction);
	}

	@Override
	public Predicate<C> getPrecondition(final AbstractGuiComponent<?, C> guiComponent) {
		return guiComponent::canEdit;
	}

}
