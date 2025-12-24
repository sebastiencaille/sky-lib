package ch.scaille.testing.testpilot;

import java.time.Duration;
import java.util.Optional;

import ch.scaille.testing.testpilot.ModalDialogDetector.Builder;
import ch.scaille.testing.testpilot.PilotReport.ReportFunction;
import ch.scaille.testing.testpilot.factories.FailureHandlers;
import ch.scaille.testing.testpilot.factories.FailureHandlers.FailureHandler;
import ch.scaille.util.helpers.DelayFunction;
import ch.scaille.util.helpers.NoExceptionCloseable;
import lombok.Getter;
import lombok.Setter;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
@Getter
@Setter
public class GuiPilot {

	private final PilotReport actionReport = new PilotReport();

	@Nullable
	private ActionDelay actionDelay = null;

	private Duration defaultModalDialogTimeout = Duration.ofSeconds(30);

	private Duration defaultPollingTimeout = Duration.ofSeconds(30);

	private Duration pollingFirstDelay = Duration.ofMillis(0);

	private DelayFunction pollingDelayFunction = p -> {
		final var elapsedTime = p.getTimeTracker().elapsedTimeMs();
		if (elapsedTime < 500) {
			return Duration.ofMillis(100);
		} else if (elapsedTime < 10_000) {
			return Duration.ofMillis(500);
		} else if (elapsedTime < 60_000) {
			return Duration.ofMillis(1_000);
		}
		return Duration.ofMillis(5_000);
	};

	private ReportFunction<Object> reportFunction = (pc, text) -> {
		if (text == null) {
			return "";
		}
		return pc.description() + ": " + text;
	};

	@Nullable
	private ModalDialogDetector currentModalDialogDetector;

	public <T extends GuiPilot> T unwrap(Class<T> target) {
		return target.cast(this);
	}

	/**
	 * Sets the delay implied by the last executed action
	 *
     */
	public void setActionDelay(@Nullable final ActionDelay actionDelay) {
		this.actionDelay = actionDelay;
	}

	public Optional<ActionDelay> getActionDelay() {
		return Optional.ofNullable(actionDelay);
	}

	protected Builder createDefaultModalDialogDetector() {
		return ModalDialogDetector.threadInterruptor();
	}

	public void waitModalDialogHandled() {
		waitModalDialogHandled(FailureHandlers.throwError());
	}

	/**
	 * Enables the existing dialog detector (or a default one) until the finally is
	 * executed
	 */
	public NoExceptionCloseable withModalDialogDetection() {
		if (currentModalDialogDetector == null) {
			currentModalDialogDetector = createDefaultModalDialogDetector().build(this);
		}
		return ModalDialogDetector.withModalDialogDetection(currentModalDialogDetector);
	}

	/**
	 * Expects a modal dialog. Use try-finally or waitModalDialogHandled if a check
	 * is required
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
	public boolean waitModalDialogHandled(final FailureHandler<ModalDialogDetector.PollingResult, Boolean> onFail) {
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
