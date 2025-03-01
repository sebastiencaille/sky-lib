package ch.scaille.testing.testpilot;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.junit.jupiter.api.Assertions;

import ch.scaille.testing.testpilot.factories.PollingResults;
import ch.scaille.testing.testpilot.factories.FailureHandlers.FailureHandler;
import ch.scaille.util.helpers.Logs;
import ch.scaille.util.helpers.NoExceptionCloseable;
import ch.scaille.util.helpers.OverridableParameter;
import ch.scaille.util.helpers.Poller;

/**
 * Handles unexpected dialog boxes, alerts, ...
 *
 * @author scaille
 *
 */
public class ModalDialogDetector {

	private static final java.util.logging.Logger LOGGER = Logs.of(ModalDialogDetector.class);

	public static PollingResult expected() {
		return new PollingResult(true, null, null, null);
	}

	public static PollingResult unhandled(final String error, final Runnable closeFunction) {
		return new PollingResult(true, error, closeFunction, null);
	}

	public static PollingResult notHandled(final String extraInfo) {
		return new PollingResult(false, null, null, extraInfo);
	}

	public static NoExceptionCloseable withModalDialogDetection(final ModalDialogDetector modalDialogDetector) {
		return modalDialogDetector.schedule(timer);
	}

	public static Builder threadInterruptor() {
		final var testThread = Thread.currentThread();
		return new Builder(Collections::emptyList, e -> testThread.interrupt());
	}

	public static class PollingResult {
		public final boolean handled;
		private final String error;
		private final String extraInfo;
		private final Runnable closeOnErrorFunction;

		private PollingResult(final boolean handled, final String error, final Runnable closeOnErrorFunction,
				final String extraInfo) {
			this.handled = handled;
			this.error = error;
			this.closeOnErrorFunction = closeOnErrorFunction;
			this.extraInfo = extraInfo;
		}

	}

	public static class Builder {

		private final Supplier<List<PollingResult>> pollingHandlers;

		private final Consumer<List<String>> dialogNotHandled;

		private final OverridableParameter<GuiPilot, Duration> timeout = new OverridableParameter<>(
				GuiPilot::getDefaultModalDialogTimeout);

		public Builder(final Supplier<List<PollingResult>> pollingHandlers,
				final Consumer<List<String>> errorsHandler) {
			this.pollingHandlers = pollingHandlers;
			this.dialogNotHandled = errorsHandler;
		}

		public Builder withTimeout(Duration duration) {
			this.timeout.set(duration);
			return this;
		}

		public ModalDialogDetector build(GuiPilot pilot) {
			return new ModalDialogDetector(this, pilot);
		}
	}

	private static final Timer timer = new Timer("Modal dialog detector");

	private final List<String> errors = new ArrayList<>();

	private PollingResult handledDialog = null;

	private int stackCount = 0;

	private final ReentrantLock running = new ReentrantLock();

	private final Builder builder;

	private TimerTask timerTask;

	public ModalDialogDetector(Builder builder, GuiPilot pilot) {
		this.builder = builder;
		builder.timeout.withSource(pilot).ensureLoaded();
	}

	private synchronized void handleModalDialogs() {
		try {
			running.lock();
			errors.clear();
			for (final var pollingResult : builder.pollingHandlers.get()) {
				if (!pollingResult.handled) {
					errors.add("Unhandled dialog box: " + pollingResult.extraInfo);
					continue;
				}
				this.handledDialog = pollingResult;
				if (pollingResult.error == null) {
					return;
				}
				// handle error
				errors.add(pollingResult.error);
				if (pollingResult.closeOnErrorFunction != null) {
					pollingResult.closeOnErrorFunction.run();
				}

			}
			if (!errors.isEmpty()) {
				builder.dialogNotHandled.accept(errors);
			}
		} finally {
			running.unlock();
		}
	}

	protected NoExceptionCloseable schedule(final Timer t) {
		if (handledDialog != null) {
			throw new IllegalStateException("The detector already detected a dialog");
		}
		LOGGER.fine(() -> "Scheduled: " + stackCount);
		if (stackCount == 0) {
			timerTask = timerTask();
			t.schedule(timerTask, 0, 500);
		}
		stackCount++;
		return this::close;
	}

	public void close() {
		try {
			running.lock();
			LOGGER.fine(() -> "Unscheduling " + stackCount);
			stackCount--;
			if (stackCount == 0) {
				timerTask.cancel();
				timerTask = null;
				Assertions.assertEquals(0, errors.size(),
						() -> "Unexpected modal dialog: " + String.join(",\n", errors));
			}
		} finally {
			running.unlock();
		}
	}

	protected TimerTask timerTask() {
		return new TimerTask() {

			@Override
			public void run() {
				handleModalDialogs();
			}

		};
	}

	public Optional<PollingResult> getPollingResult(Poller poller) {
		try {
			running.lock();
			return Optional.ofNullable(handledDialog);
		} finally {
			running.unlock();
		}
	}

	public boolean waitModalDialogHandled(final FailureHandler<ModalDialogDetector.PollingResult, Boolean> onFail) {
		return new Poller(builder.timeout.get(), Duration.ofMillis(100), p -> Duration.ofMillis(100))
				.run(this::getPollingResult, Objects::nonNull)
				.map(p -> true)
				.orElseGet(() -> {
					onFail.apply(PollingResults.failure("Modal dialog not detected"));
					return false;
				});
	}

	public boolean isRunning() {
		return timerTask != null;
	}

}
