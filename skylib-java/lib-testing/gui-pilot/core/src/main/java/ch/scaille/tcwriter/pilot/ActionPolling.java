package ch.scaille.tcwriter.pilot;

import java.util.Optional;
import java.util.function.Predicate;

public class ActionPolling<C, V> extends Polling<C, V> {

	public ActionPolling(final PollingFunction<C, V> pollingFunction) {
		super(null, pollingFunction);
	}

	public ActionPolling(final Predicate<C> precondition, final PollingFunction<C, V> pollingFunction) {
		super(precondition, pollingFunction);
	}

	@Override
	public Optional<Predicate<C>> getPrecondition(final AbstractComponentPilot<?, C> guiComponent) {
		return Optional.of(guiComponent::canEdit);
	}

}
