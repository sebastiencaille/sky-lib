package ch.scaille.tcwriter.pilot;

import java.util.Optional;
import java.util.function.Predicate;

public class StatePolling<C, V> extends Polling<C, V> {

	public StatePolling(final PollingFunction<C, V> pollingFunction) {
		super(null, pollingFunction);
	}

	public StatePolling(final Predicate<C> precondition, final PollingFunction<C, V> pollingFunction) {
		super(precondition, pollingFunction);
	}

	@Override
	public Optional<Predicate<C>> getPrecondition(final AbstractComponentPilot<?, C> guiComponent) {
		return super.getPrecondition(guiComponent).or(() -> Optional.of(guiComponent::canCheck));
	}

}
