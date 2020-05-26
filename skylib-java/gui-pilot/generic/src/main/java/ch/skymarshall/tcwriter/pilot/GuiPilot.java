package ch.skymarshall.tcwriter.pilot;

import java.time.Duration;

import org.junit.Assert;

import ch.skymarshall.util.helpers.NoExceptionCloseable;

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
		final long start = System.currentTimeMillis();
		while (System.currentTimeMillis() - start < defaultActionTimeout.toMillis()) {
			if (currentModalDialogDetector.getCheckResult() != null) {
				stopModalDialogDetector();
				return;
			}
			try {
				Thread.sleep(100);
			} catch (final InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
		Assert.fail("Modal dialog not detected");
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
