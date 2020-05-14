package ch.skymarshall.tcwriter.pilot.swing;

import static ch.skymarshall.tcwriter.pilot.Polling.failure;
import static ch.skymarshall.tcwriter.pilot.Polling.matches;
import static ch.skymarshall.tcwriter.pilot.Polling.throwError;

import java.awt.event.KeyEvent;
import java.lang.reflect.InvocationTargetException;
import java.time.Duration;
import java.util.function.Predicate;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import ch.skymarshall.tcwriter.pilot.AbstractGuiComponent;
import ch.skymarshall.tcwriter.pilot.Polling;
import ch.skymarshall.tcwriter.pilot.Polling.PollingFunction;
import ch.skymarshall.tcwriter.pilot.Polling.PollingResultFunction;

public class AbstractSwingComponent<T extends JComponent> extends AbstractGuiComponent<T, AbstractSwingComponent<T>> {

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
	protected T loadElement() {
		try {
			return pilot.getComponent(name, clazz);
		} catch (final NoSuchComponentException e) {
			return null;
		}
	}

	protected boolean canRead(final T component) {
		return component.isVisible();
	}

	protected boolean canEdit(final T component) {
		return component.isEnabled() && component.isVisible();
	}

	@Override
	protected <U> U waitActionSuccess(final Predicate<T> precondition, final PollingFunction<T, U> applier,
			final Duration timeout, final PollingResultFunction<T, U> onFail) {
		if (SwingUtilities.isEventDispatchThread()) {
			throw new IllegalStateException("Action must not run in Swing thread");
		}
		return super.waitActionSuccess(precondition, applier, timeout, onFail);
	}

	@Override
	protected <U> Polling<T, U> executePolling(final Predicate<T> precondition, final PollingFunction<T, U> applier) {
		final Object[] response = new Object[1];
		try {
			SwingUtilities.invokeAndWait(() -> response[0] = super.executePolling(precondition, applier));
		} catch (final InvocationTargetException e) {
			throw new IllegalStateException("Polling failed with unexpected exception", e);
		} catch (final InterruptedException e) {
			Thread.currentThread().interrupt();
			return failure("Interrupted");
		}
		return (Polling<T, U>) response[0];
	}

	public <U> U waitEditSuccess(final PollingFunction<T, U> applier) {
		return waitActionSuccess(this::canEdit, applier, pilot.getDefaultActionTimeout(), throwError());
	}

	public <U> U waitEditSuccess(final PollingFunction<T, U> applier, final PollingResultFunction<T, U> onFail) {
		return waitActionSuccess(this::canEdit, applier, pilot.getDefaultActionTimeout(), onFail);
	}

	public <U> U waitState(final PollingFunction<T, U> applier) {
		return waitActionSuccess(this::canRead, applier, pilot.getDefaultActionTimeout(), throwError());
	}

	public void waitEnabled() {
		withReport(c -> "check enabled").waitState(matches(JComponent::isEnabled));
	}

	public void waitDisabled() {
		withReport(c -> "check disabled").waitState(matches(c -> !c.isEnabled()));
	}

	public static void pressReturn(final JComponent t) {
		t.dispatchEvent(new KeyEvent(t, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, KeyEvent.VK_ENTER, '\n'));
	}
}
