package ch.scaille.tcwriter.pilot.swing;

import java.util.Optional;
import java.util.function.Function;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import ch.scaille.tcwriter.pilot.AbstractComponentPilot;
import ch.scaille.tcwriter.pilot.Factories;
import ch.scaille.tcwriter.pilot.Factories.Pollings;
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
	protected Optional<C> loadGuiComponent() {
		try {
			return Optional.of(pilot.getComponent(name, clazz));
		} catch (final NoSuchComponentException e) {
			return Optional.empty();
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
	protected <P, U> U waitPollingSuccess(Polling<C, P> polling, Function<P, U> successTransformer,
			FailureHandler<C, P, U> onFail) {
		if (SwingUtilities.isEventDispatchThread()) {
			throw new IllegalStateException("Polling must not run in Swing thread");
		}
		return super.waitPollingSuccess(polling, successTransformer, onFail);
	}

	@Override
	protected <U> Optional<PollingResult<C, U>> executePolling(Poller poller, final Polling<C, U> polling) {
		final var response = new Optional[1];
		SwingHelper.invokeAndWait(() -> response[0] = super.executePolling(poller, polling));
		return response[0];
	}

	public void assertEnabled() {
		polling(Pollings.<C>satisfies(JComponent::isEnabled)
				.withReportText(Factories.Reporting.checkingThat("component is enabled"))).orFail();
	}

	public void assertDisabled() {
		polling(Pollings.<C>satisfies(c -> !c.isEnabled())
				.withReportText(Factories.Reporting.checkingThat("component is disabled"))).orFail();
	}

}
