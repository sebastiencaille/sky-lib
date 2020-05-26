package ch.skymarshall.tcwriter.pilot.swing;

import static ch.skymarshall.tcwriter.pilot.Polling.satisfies;

import java.awt.event.KeyEvent;
import java.time.Duration;
import java.util.function.Predicate;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import ch.skymarshall.tcwriter.pilot.AbstractGuiComponent;
import ch.skymarshall.tcwriter.pilot.Polling;
import ch.skymarshall.tcwriter.pilot.Polling.PollingFunction;
import ch.skymarshall.tcwriter.pilot.Polling.PollingResultFunction;

public class AbstractSwingComponent<C extends AbstractSwingComponent<C, T>, T extends JComponent>
		extends AbstractGuiComponent<C, T> {

	protected final SwingGuiPilot pilot;
	protected final String name;
	protected final Class<T> clazz;

	public AbstractSwingComponent(final SwingGuiPilot pilot, final Class<T> clazz, final String name) {
		super(pilot);
		this.pilot = pilot;
		this.name = name;
		this.clazz = clazz;
	}

	@Override
	public String toString() {
		return clazz.getSimpleName() + "{" + name + "}";
	}

	@Override
	protected T loadGuiComponent() {
		try {
			return pilot.getComponent(name, clazz);
		} catch (final NoSuchComponentException e) {
			return null;
		}
	}

	@Override
	protected boolean canCheck(final T component) {
		return component.isVisible();
	}

	@Override
	protected boolean canEdit(final T component) {
		return component.isVisible() && component.isEnabled();
	}

	@Override
	protected <U> U waitActionSuccess(final Predicate<T> precondition, final PollingFunction<T, U> applier,
			final Duration timeout, final PollingResultFunction<T, U> onFail) {
		if (SwingUtilities.isEventDispatchThread()) {
			throw new IllegalStateException("Action wait must not run in Swing thread");
		}
		return super.waitActionSuccess(precondition, applier, timeout, onFail);
	}

	@Override
	protected <U> Polling<T, U> executePolling(final Predicate<T> precondition, final PollingFunction<T, U> applier) {
		final Object[] response = new Object[1];
		SwingGuiPilot.invokeAndWait(() -> response[0] = super.executePolling(precondition, applier));
		return (Polling<T, U>) response[0];
	}

	public void waitEnabled() {
		withReport(c -> "check enabled").waitState(satisfies(JComponent::isEnabled));
	}

	public void waitDisabled() {
		withReport(c -> "check disabled").waitState(satisfies(c -> !c.isEnabled()));
	}

	public static void pressReturn(final JComponent t) {
		t.dispatchEvent(new KeyEvent(t, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, KeyEvent.VK_ENTER, '\n'));
	}
}
