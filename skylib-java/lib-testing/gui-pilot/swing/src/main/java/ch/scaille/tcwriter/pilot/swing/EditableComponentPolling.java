package ch.scaille.tcwriter.pilot.swing;

import java.util.Optional;
import java.util.function.Predicate;

import javax.swing.JComponent;

import ch.scaille.tcwriter.pilot.AbstractComponentPilot;
import ch.scaille.tcwriter.pilot.Polling;

public class EditableComponentPolling<V> extends Polling<JComponent, V> {

	public EditableComponentPolling(final PollingFunction<JComponent, V> pollingFunction) {
		super(null, pollingFunction);
	}

	public EditableComponentPolling(final Predicate<JComponent> precondition, final PollingFunction<JComponent, V> pollingFunction) {
		super(precondition, pollingFunction);
	}

	@Override
	public Optional<Predicate<JComponent>> getPrecondition(final AbstractComponentPilot<?, JComponent> guiComponentPilot) {
		return Optional.of(c -> super.getPrecondition(guiComponentPilot).map(p -> p.test(c)).orElse(true)
				&& ((AbstractSwingComponent<?, JComponent>)guiComponentPilot).canEdit(c));
	}

}
