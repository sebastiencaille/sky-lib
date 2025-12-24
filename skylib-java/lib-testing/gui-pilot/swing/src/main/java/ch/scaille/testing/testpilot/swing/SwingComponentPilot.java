package ch.scaille.testing.testpilot.swing;

import java.util.Optional;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import ch.scaille.testing.testpilot.AbstractComponentPilot;
import ch.scaille.testing.testpilot.Polling;
import ch.scaille.testing.testpilot.PolledComponent;
import ch.scaille.testing.testpilot.PollingResult;
import ch.scaille.util.helpers.Poller;
import org.jspecify.annotations.NullMarked;

/**
 * An abstract class for Swing component pilots
 * @param <C> the type of the Swing Component
 */
@NullMarked
public class SwingComponentPilot<C extends JComponent>
		extends AbstractComponentPilot<C> {

	protected final SwingPilot pilot;
	protected final String name;
	protected final Class<C> clazz;

	public SwingComponentPilot(final SwingPilot pilot, final Class<C> clazz, final String name) {
		super(pilot);
		this.pilot = pilot;
		this.name = name;
		this.clazz = clazz;
	}

	@Override
	protected Optional<String> getDescription() {
		return Optional.of(toString());
	}

	@Override
	public String toString() {
		return clazz.getSimpleName() + "[" + name + "]";
	}

	@Override
	protected Optional<C> loadGuiComponent() {
		try {
			return Optional.of(pilot.getComponent(name, clazz));
		} catch (final NoSuchComponentException e) {
			return Optional.empty();
		}
	}

	@Override
	public boolean canCheck(final PolledComponent<C> ctxt) {
		return ctxt.component().isVisible();
	}

	protected boolean canEdit(final PolledComponent<C> ctxt) {
		return ctxt.component().isVisible() && ctxt.component().isEnabled();
	}

	@Override
	public <V> PollingResult<C, V> waitPollingSuccess(final Polling.PollingBuilder<C, V> polling) {
		if (SwingUtilities.isEventDispatchThread()) {
			throw new IllegalStateException("Polling must not run in Swing thread");
		}
		return super.waitPollingSuccess(polling);
	}

	@Override
	protected <U> Optional<PollingResult<C, U>> executePolling(Poller poller, final Polling<C, U>.InitializedPolling polling) {
		return SwingHelper.invokeAndWait(() -> super.executePolling(poller, polling));
	}

}
