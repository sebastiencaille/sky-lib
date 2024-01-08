package ch.scaille.tcwriter.pilot;

import java.time.Duration;

import ch.scaille.tcwriter.pilot.Factories.FailureHandlers;
import ch.scaille.tcwriter.pilot.ModalDialogDetector.Builder;
import ch.scaille.tcwriter.pilot.PilotReport.ReportFunction;
import ch.scaille.util.helpers.NoExceptionCloseable;
import ch.scaille.util.helpers.Poller;

public class GuiPilot {

	private final PilotReport actionReport = new PilotReport();

	private ActionDelay actionDelay = null;

	private Duration defaultModalDialogTimeout = Duration.ofSeconds(30);

	private Duration pollingTimeout = Duration.ofSeconds(30);

	private Duration pollingFirstDelay = Duration.ofMillis(0);

	private Poller.DelayFunction pollingDelayFunction = p -> {
		final var elapsedTime = p.getTimeTracker().elapsedTimeMs();
		if (elapsedTime < 500) {
			return Duration.ofMillis(50);
		} else if (elapsedTime < 10_000) {
			return Duration.ofMillis(250);
		} else if (elapsedTime < 60_000) {
			return Duration.ofMillis(1_000);
		}
		return Duration.ofMillis(5_000);
	};

	private ReportFunction<Object> reportFunction = (pc, text) -> {
		if (text == null) {
			return "";
		}
		return pc.description + ": " + text;
	};

	private ModalDialogDetector currentModalDialogDetector;

	public PilotReport getActionReport() {
		return actionReport;
	}

	/**
	 * Sets the delay implied by the last executed action
	 * 
	 * @param actionDelay
	 */
	public void setActionDelay(final ActionDelay actionDelay) {
		this.actionDelay = actionDelay;
	}

	public ActionDelay getActionDelay() {
		return actionDelay;
	}

	public Duration getPollingTimeout() {
		return pollingTimeout;
	}

	public void setDefaultPollingTimeout(final Duration pollingTimeout) {
		this.pollingTimeout = pollingTimeout;
	}

	public Duration getPollingFirstDelay() {
		return pollingFirstDelay;
	}

	public void setPollingFirstDelay(Duration pollingFirstDelay) {
		this.pollingFirstDelay = pollingFirstDelay;
	}

	public ReportFunction<Object> getReportFunction() {
		return reportFunction;
	}

	public void setReportFunction(ReportFunction<Object> reportFunction) {
		this.reportFunction = reportFunction;
	}

	public Poller.DelayFunction getPollingDelayFunction() {
		return pollingDelayFunction;
	}

	public void setPollingDelayFunction(Poller.DelayFunction pollingDelayFunction) {
		this.pollingDelayFunction = pollingDelayFunction;
	}

	protected Builder createDefaultModalDialogDetector() {
		return ModalDialogDetector.threadInterruptor();
	}

	public void waitModalDialogHandled() {
		waitModalDialogHandled(FailureHandlers.throwError());
	}

	public Duration getDefaultModalDialogTimeout() {
		return defaultModalDialogTimeout;
	}

	public void setDefaultModalDialogTimeout(Duration modalDialogTimeout) {
		this.defaultModalDialogTimeout = modalDialogTimeout;
	}

	/**
	 * Enables the existing dialog detector (or a default one) until the finally is executed
	 */
	public NoExceptionCloseable withModalDialogDetection() {
		if (currentModalDialogDetector == null) {
			currentModalDialogDetector = createDefaultModalDialogDetector().build(this);
		}
		return ModalDialogDetector.withModalDialogDetection(currentModalDialogDetector);
	}

	/**
	 * Expects a modal dialog. Use try-finally or waitModalDialogHandled if a check is required 
	 */
	protected NoExceptionCloseable expectModalDialog(ModalDialogDetector.Builder detector) {
		if (currentModalDialogDetector != null && currentModalDialogDetector.isRunning()) {
			throw new IllegalStateException("The previous detector is still running.");
		}
		currentModalDialogDetector = detector.build(this);
		return ModalDialogDetector.withModalDialogDetection(currentModalDialogDetector);
	}

	/**
	 * Wait the model dialog expected by expectModalDialog
	 */
	public boolean waitModalDialogHandled(
			final PollingResult.FailureHandler<ModalDialogDetector.PollingResult, Boolean> onFail) {
		if (currentModalDialogDetector == null) {
			throw new IllegalStateException("expectModalDialog was never called");
		}
		try {
			return currentModalDialogDetector.waitModalDialogHandled(onFail);
		} finally {
			currentModalDialogDetector.close();
			currentModalDialogDetector = null;
		}
	}

	public void close() {
		if (currentModalDialogDetector != null) {
			currentModalDialogDetector.close();
		}
		currentModalDialogDetector = null;
	}

}
