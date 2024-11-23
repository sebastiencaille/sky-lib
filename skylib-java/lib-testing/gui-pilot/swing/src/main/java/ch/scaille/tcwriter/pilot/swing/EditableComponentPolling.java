package ch.scaille.tcwriter.pilot.swing;

import java.util.Optional;
import java.util.function.Predicate;

import javax.swing.JComponent;

import ch.scaille.tcwriter.pilot.Polling;
import ch.scaille.tcwriter.pilot.PollingContext;

public class EditableComponentPolling<V> extends Polling<JComponent, V> {

	public EditableComponentPolling(final PollingFunction<JComponent, V> pollingFunction) {
		super(null, pollingFunction);
	}

	public EditableComponentPolling(final Predicate<PollingContext<JComponent>> precondition,
			final PollingFunction<JComponent, V> pollingFunction) {
		super(precondition, pollingFunction);
	}

	@Override
	public Optional<Predicate<PollingContext<JComponent>>> getPrecondition() {
		return Optional.of(c -> super.getPrecondition().map(p -> p.test(c)).orElse(true)
				&& ((SwingComponentPilot<JComponent>) c.getPilot()).canEdit(c));
	}

}
