package ch.scaille.tcwriter.pilot.swing;

import java.util.Optional;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import ch.scaille.tcwriter.pilot.AbstractComponentPilot;
import ch.scaille.tcwriter.pilot.Polling;
import ch.scaille.tcwriter.pilot.PollingContext;
import ch.scaille.tcwriter.pilot.PollingResult;
import ch.scaille.util.helpers.Poller;
/**
 * An abstract class for Swing component pilots
 * @param <G> the type of the Swing Component Pilot
 * @param <C> the type of the Swing Component
 */
public class AbstractSwingComponentPilot<G extends AbstractSwingComponentPilot<G, C>, C extends JComponent>
		extends AbstractComponentPilot<G, C> {

	protected final SwingPilot pilot;
	protected final String name;
	protected final Class<C> clazz;

	public AbstractSwingComponentPilot(final SwingPilot pilot, final Class<C> clazz, final String name) {
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
	protected boolean canCheck(final PollingContext<C> ctxt) {
		return ctxt.getComponent().isVisible();
	}

	protected boolean canEdit(final PollingContext<C> ctxt) {
		return ctxt.getComponent().isVisible() && ctxt.getComponent().isEnabled();
	}

	@Override
	public <P> PollingResult<C, P> waitPollingSuccess(final Polling<C, P> polling) {
		if (SwingUtilities.isEventDispatchThread()) {
			throw new IllegalStateException("Polling must not run in Swing thread");
		}
		return super.waitPollingSuccess(polling);
	}

	@Override
	protected <U> Optional<PollingResult<C, U>> executePolling(Poller poller, final Polling<C, U> polling) {
		final var response = new Optional[1];
		SwingHelper.invokeAndWait(() -> response[0] = super.executePolling(poller, polling));
		return response[0];
	}

	@Override
	public SwingPollingBuilder<G, C> polling() {
		return new SwingPollingBuilder<>(this);
	}

	public void assertEnabled() {
		polling().tryApply(JComponent::isEnabled).orFail("Component is not enabled");
	}
	

	public void assertDisabled() {
		polling().trySatisfy(comp -> !comp.isEnabled()).orFail("Component is not enabled");
	}
}
