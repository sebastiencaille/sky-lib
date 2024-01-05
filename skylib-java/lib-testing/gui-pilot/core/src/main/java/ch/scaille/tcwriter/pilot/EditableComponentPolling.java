package ch.scaille.tcwriter.pilot;

import java.util.Optional;
import java.util.function.Predicate;

public class EditableComponentPolling<C, V> extends Polling<C, V> {

	public EditableComponentPolling(final PollingFunction<C, V> pollingFunction) {
		super(null, pollingFunction);
	}

	public EditableComponentPolling(final Predicate<C> precondition, final PollingFunction<C, V> pollingFunction) {
		super(precondition, pollingFunction);
	}

	@Override
	public Optional<Predicate<C>> getPrecondition(final AbstractComponentPilot<?, C> guiComponentPilot) {
		return Optional.of(c -> super.getPrecondition(guiComponentPilot).map(p -> p.test(c)).orElse(true)
				&& guiComponentPilot.canEdit(c));
	}

}
