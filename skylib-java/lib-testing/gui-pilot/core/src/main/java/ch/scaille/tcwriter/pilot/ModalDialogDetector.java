package ch.scaille.tcwriter.pilot;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.junit.jupiter.api.Assertions;

import ch.scaille.tcwriter.pilot.Factories.PollingResults;
import ch.scaille.tcwriter.pilot.PollingResult.FailureHandler;
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

	public static PollingResult expected() {
		return new PollingResult(true, null, null, null);
	}

	public static PollingResult error(final String error, final Runnable closeFunction) {
		return new PollingResult(true, error, closeFunction, null);
	}

	public static PollingResult notHandled(final String extraInfo) {
		return new PollingResult(false, null, null, extraInfo);
	}

	public static NoExceptionCloseable withModalDialogDetection(final ModalDialogDetector modalDialogDetector) {
		return modalDialogDetector.schedule(timer);
	}

	public static ModalDialogDetector noDetection() {
		final var testThread = Thread.currentThread();
		return new ModalDialogDetector(null, e -> testThread.interrupt()) {
			@Override
			protected synchronized NoExceptionCloseable schedule(Timer t) {
				return () -> {
					/* noop */ };
			}
		};
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

	private static final Timer timer = new Timer("Modal dialog detector");

	private final Supplier<List<PollingResult>> pollingResults;

	private final OverridableParameter<GuiPilot, Duration> timeout = new OverridableParameter<>(
			GuiPilot::getModalDialogTimeout);

	private final Consumer<List<String>> dialogNotHandled;

	private GuiPilot pilot;

	private final List<String> errors = new ArrayList<>();

	private PollingResult handledDialog = null;

	private boolean enabled = false;

	private final Semaphore running = new Semaphore(1);

	public ModalDialogDetector(final Supplier<List<PollingResult>> pollingHandlers,
			final Consumer<List<String>> errorsHandler) {
		this.pollingResults = pollingHandlers;
		this.dialogNotHandled = errorsHandler;
	}

	public ModalDialogDetector initialize(GuiPilot pilot) {
		timeout.withSource(pilot).ensureLoaded();
		this.pilot = pilot;
		return this;
	}

	public ModalDialogDetector withTimeout(Duration duration) {
		this.timeout.set(duration);
		return this;
	}

	private synchronized void handleModalDialogs() {
		try {
			errors.clear();
			running.acquire();
			for (final var pollingResult : pollingResults.get()) {
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
				dialogNotHandled.accept(errors);
			}
		} catch (final InterruptedException e) {
			Thread.currentThread().interrupt();
			// noop
		} finally {
			running.release();
		}
	}

	public void close() {
		Assertions.assertEquals("", String.join(",\n", errors), () -> "Unexpected modal dialog");
	}

	protected TimerTask timerTask() {
		return new TimerTask() {

			@Override
			public void run() {
				if (!enabled) {
					this.cancel();
					return;
				}
				handleModalDialogs();
			}

		};
	}

	public void stop() {
		enabled = false;
		try {
			running.acquire();
		} catch (final InterruptedException e) {
			Thread.currentThread().interrupt();
			Assertions.fail(e.getMessage());
		}
	}

	protected synchronized NoExceptionCloseable schedule(final Timer t) {
		Assertions.assertNull(handledDialog, () -> "Detector already detected a dialog");
		if (!enabled) {
			enabled = true;
			t.schedule(timerTask(), 0, 500);
		}
		return this::close;
	}

	public synchronized Optional<PollingResult> getPollingResult(Poller poller) {
		return Optional.ofNullable(handledDialog);
	}

	public boolean waitModalDialogHandled(final FailureHandler<ModalDialogDetector.PollingResult, Boolean> onFail) {
		return new Poller(timeout.get(), Duration.ofMillis(100), p -> Duration.ofMillis(100))
				.run(this::getPollingResult, Objects::nonNull)
				.map(p -> true)
				.orElseGet(() -> onFail.apply(PollingResults.failure("Modal dialog not detected"), pilot));
	}

}
