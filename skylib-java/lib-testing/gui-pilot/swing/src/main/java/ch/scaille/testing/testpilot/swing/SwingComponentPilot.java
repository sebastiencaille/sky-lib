package ch.scaille.testing.testpilot.swing;

import java.util.Optional;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import ch.scaille.testing.testpilot.AbstractComponentPilot;
import ch.scaille.testing.testpilot.Polling;
import ch.scaille.testing.testpilot.PollingContext;
import ch.scaille.testing.testpilot.PollingResult;
import ch.scaille.util.helpers.Poller;
/**
 * An abstract class for Swing component pilots
 * @param <C> the type of the Swing Component
 */
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
	public boolean canCheck(final PollingContext<C> ctxt) {
		return ctxt.getComponent().isVisible();
	}

	protected boolean canEdit(final PollingContext<C> ctxt) {
		return ctxt.getComponent().isVisible() && ctxt.getComponent().isEnabled();
	}

	@Override
	public <V> PollingResult<C, V> waitPollingSuccess(final Polling<C, V> polling) {
		if (SwingUtilities.isEventDispatchThread()) {
			throw new IllegalStateException("Polling must not run in Swing thread");
		}
		return super.waitPollingSuccess(polling);
	}

	@Override
	protected <U> Optional<PollingResult<C, U>> executePolling(Poller poller, final Polling<C, U> polling) {
		return SwingHelper.invokeAndWait(() -> super.executePolling(poller, polling));
	}

}
