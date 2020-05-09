package ch.skymarshall.tcwriter.pilot.swing;

import static ch.skymarshall.tcwriter.pilot.Polling.assertFail;
import static ch.skymarshall.tcwriter.pilot.Polling.failure;
import static ch.skymarshall.tcwriter.pilot.Polling.matches;
import static ch.skymarshall.tcwriter.pilot.Polling.throwError;

import java.lang.reflect.InvocationTargetException;
import java.time.Duration;
import java.util.function.Function;
import java.util.function.Predicate;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import ch.skymarshall.tcwriter.pilot.AbstractGuiComponent;
import ch.skymarshall.tcwriter.pilot.Polling;

public class AbstractSwingComponent<T extends JComponent> extends AbstractGuiComponent<T, AbstractSwingComponent<T>> {

	protected final GuiPilot pilot;
	protected final String name;
	protected final Class<T> clazz;

	public AbstractSwingComponent(final GuiPilot pilot, final Class<T> clazz, final String name) {
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
	protected <U> U waitActionSuccess(final Predicate<T> precondition, final Function<T, Polling<T, U>> applier,
			final Duration timeout, final Function<Polling<T, U>, U> onFail) {
		if (SwingUtilities.isEventDispatchThread()) {
			throw new IllegalStateException("Action must not run in Swing thread");
		}
		return super.waitActionSuccess(precondition, applier, timeout, onFail);
	}

	@Override
	protected <U> Polling<T, U> executePolling(final Predicate<T> precondition,
			final Function<T, Polling<T, U>> applier) {
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

	public <U> U waitComponentEditSuccess(final Function<T, Polling<T, U>> applier,
			final Function<Polling<T, U>, U> onFail) {
		return waitActionSuccess(this::canEdit, applier, pilot.getDefaultActionTimeout(), onFail);
	}

	public <U> U waitEditSuccess(final Function<T, Polling<T, U>> applier) {
		return waitActionSuccess(this::canEdit, applier, pilot.getDefaultActionTimeout(), throwError());
	}

	public <U> U waitComponentReadSuccess(final Function<T, Polling<T, U>> applier,
			final Function<Polling<T, U>, U> onFail) {
		return waitActionSuccess(this::canRead, applier, pilot.getDefaultActionTimeout(), onFail);
	}

	public <U> U waitReadSuccess(final Function<T, Polling<T, U>> applier) {
		return waitActionSuccess(this::canRead, applier, pilot.getDefaultActionTimeout(), throwError());
	}

	public <U> U waitComponentEditSuccess(final Function<T, Polling<T, U>> applier, final String reason) {
		return waitComponentEditSuccess(applier, assertFail(reason));
	}

	public void waitEnabled() {
		withReport(c -> "check enabled");
		waitReadSuccess(matches(JComponent::isEnabled));
	}

	public void waitDisabled() {
		withReport(c -> "check disabled");
		waitReadSuccess(matches(c -> !c.isEnabled()));
	}
}
