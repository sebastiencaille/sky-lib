package ch.skymarshall.tcwriter.pilot;

import java.time.Duration;

import ch.skymarshall.util.helpers.NoExceptionCloseable;
import ch.skymarshall.util.helpers.Timeout;

public class GuiPilot {

	private final ActionReport actionReport = new ActionReport();

	private ActionDelay actionDelay = null;

	private Duration defaultActionTimeout = Duration.ofSeconds(30);

	private ModalDialogDetector currentModalDialogDetector;

	public ActionReport getActionReport() {
		return actionReport;
	}

	public void setActionDelay(final ActionDelay actionDelay) {
		this.actionDelay = actionDelay;
	}

	public ActionDelay getActionDelay() {
		return actionDelay;
	}

	public void setDefaultActionTimeout(final Duration defaultActionTimeout) {
		this.defaultActionTimeout = defaultActionTimeout;
	}

	public Duration getDefaultActionTimeout() {
		return defaultActionTimeout;
	}

	protected ModalDialogDetector createDefaultModalDialogDetector() {
		return ModalDialogDetector.noDetection();
	}

	public void setCurrentModalDialogDetector(final ModalDialogDetector currentModalDialogDetector) {
		if (currentModalDialogDetector != null) {
			stopModalDialogDetector();
		}
		this.currentModalDialogDetector = currentModalDialogDetector;
	}

	private void stopModalDialogDetector() {
		this.currentModalDialogDetector.stop();
		this.currentModalDialogDetector = null;
	}

	public void waitModalDialogHandled() {
		waitModalDialogHandled(PollingResult.throwError());
	}

	public boolean waitModalDialogHandled(
			final PollingResult.PollingResultFunction<ModalDialogDetector.ErrorCheck, Boolean> onFail) {
		final Timeout timeoutCheck = new Timeout(defaultActionTimeout);
		while (!timeoutCheck.hasTimedOut()) {
			if (currentModalDialogDetector.getCheckResult() != null) {
				stopModalDialogDetector();
				return true;
			}
			try {
				timeoutCheck.yield();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				return onFail.apply(PollingResult.failure("Interrupted"), this);
			}
		}
		return onFail.apply(PollingResult.failure("Modal dialog not detected"), this);
	}

	public NoExceptionCloseable withModalDialogDetection() {
		if (currentModalDialogDetector == null) {
			currentModalDialogDetector = createDefaultModalDialogDetector();
		}
		return ModalDialogDetector.withModalDialogDetection(currentModalDialogDetector);
	}

	public void close() {
		stopModalDialogDetector();
	}
}
