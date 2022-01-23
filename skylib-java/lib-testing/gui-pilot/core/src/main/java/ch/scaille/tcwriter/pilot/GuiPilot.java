package ch.scaille.tcwriter.pilot;

import java.time.Duration;

import ch.scaille.tcwriter.pilot.Factories.FailureHandlers;
import ch.scaille.tcwriter.pilot.PilotReport.ReportFunction;
import ch.scaille.util.helpers.NoExceptionCloseable;
import ch.scaille.util.helpers.Poller;

public class GuiPilot {

	private final PilotReport actionReport = new PilotReport();

	private ActionDelay actionDelay = null;

	private Duration modalDialogTimeout = Duration.ofSeconds(30);

	private Duration pollingTimeout = Duration.ofSeconds(30);

	private Duration pollingFirstDelay = Duration.ofMillis(0);

	private Poller.DelayFunction pollingDelayFunction = p -> {
		Duration effectiveTimeout = p.timeTracker.getDuration();
		if (effectiveTimeout.toMillis() < 500) {
			return Duration.ofMillis(50);
		} else if (effectiveTimeout.toMillis() < 10_000) {
			return Duration.ofMillis(250);
		} else if (effectiveTimeout.toMillis() < 60_000) {
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

	protected ModalDialogDetector createDefaultModalDialogDetector() {
		return ModalDialogDetector.noDetection();
	}

	public void setCurrentModalDialogDetector(final ModalDialogDetector currentModalDialogDetector) {
		if (currentModalDialogDetector != null) {
			stopModalDialogDetector();
		}
		this.currentModalDialogDetector = currentModalDialogDetector;
	}

	public void waitModalDialogHandled() {
		waitModalDialogHandled(FailureHandlers.throwError());
	}

	public Duration getModalDialogTimeout() {
		return modalDialogTimeout;
	}

	public void setModalDialogTimeout(Duration modalDialogTimeout) {
		this.modalDialogTimeout = modalDialogTimeout;
	}

	protected ModalDialogDetector expectModalDialog(ModalDialogDetector detector) {
		stopModalDialogDetector();
		currentModalDialogDetector = detector.initialize(this);
		return detector;
	}

	public boolean waitModalDialogHandled(
			final PollingResult.FailureHandler<ModalDialogDetector.PollingResult, Boolean> onFail) {
		try {
			return currentModalDialogDetector.waitModalDialogHandled(onFail);
		} finally {
			stopModalDialogDetector();
		}
	}

	public NoExceptionCloseable withModalDialogDetection() {
		if (currentModalDialogDetector == null) {
			currentModalDialogDetector = createDefaultModalDialogDetector();
		}
		return ModalDialogDetector.withModalDialogDetection(currentModalDialogDetector);
	}

	private void stopModalDialogDetector() {
		if (currentModalDialogDetector != null) {
			this.currentModalDialogDetector.stop();
		}
		this.currentModalDialogDetector = null;
	}

	public void close() {
		stopModalDialogDetector();
	}

}
