package ch.scaille.tcwriter.pilot.swing;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import ch.scaille.tcwriter.pilot.AbstractComponentPilot;
import ch.scaille.tcwriter.pilot.Factories;
import ch.scaille.tcwriter.pilot.Polling;
import ch.scaille.tcwriter.pilot.PollingResult;
import ch.scaille.tcwriter.pilot.PollingResult.FailureHandler;
import ch.scaille.util.helpers.Poller;

public class AbstractSwingComponent<G extends AbstractSwingComponent<G, C>, C extends JComponent>
		extends AbstractComponentPilot<G, C> {

	protected final SwingPilot pilot;
	protected final String name;
	protected final Class<C> clazz;

	public AbstractSwingComponent(final SwingPilot pilot, final Class<C> clazz, final String name) {
		super(pilot);
		this.pilot = pilot;
		this.name = name;
		this.clazz = clazz;
	}

	@Override
	protected String getDescription() {
		return toString();
	}

	@Override
	public String toString() {
		return clazz.getSimpleName() + "[" + name + "]";
	}

	@Override
	protected String reportNameOf(C c) {
		return c.getClass().getSimpleName() + "[" + c.getName() + "]";
	}

	@Override
	protected C loadGuiComponent() {
		try {
			return pilot.getComponent(name, clazz);
		} catch (final NoSuchComponentException e) {
			return null;
		}
	}

	@Override
	protected boolean canCheck(final C component) {
		return component.isVisible();
	}

	@Override
	protected boolean canEdit(final C component) {
		return component.isVisible() && component.isEnabled();
	}

	@Override
	protected <U> U waitPollingSuccess(final Polling<C, U> polling, final FailureHandler<C, U> onFail) {
		if (SwingUtilities.isEventDispatchThread()) {
			throw new IllegalStateException("Polling must not run in Swing thread");
		}
		return super.waitPollingSuccess(polling, onFail);
	}

	@Override
	protected <U> PollingResult<C, U> executePolling(Poller poller, final Polling<C, U> polling) {
		final Object[] response = new Object[1];
		SwingHelper.invokeAndWait(() -> response[0] = super.executePolling(poller, polling));
		return (PollingResult<C, U>) response[0];
	}

	public void waitEnabled() {
		wait(Factories.<C>satisfies(JComponent::isEnabled)
				.withReportText(Factories.checkingThat("component is enabled")));
	}

	public void waitDisabled() {
		wait(Factories.<C>satisfies(c -> !c.isEnabled())
				.withReportText(Factories.checkingThat("component is disabled")));
	}


}
