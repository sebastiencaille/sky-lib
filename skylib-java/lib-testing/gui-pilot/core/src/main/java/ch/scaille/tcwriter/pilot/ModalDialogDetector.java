package ch.scaille.tcwriter.pilot;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
@SuppressWarnings("java:S5960")
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
		final Thread testThread = Thread.currentThread();
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

	private final Supplier<List<PollingResult>> pollingHandlers;

	private final OverridableParameter<GuiPilot, Duration> timeout = new OverridableParameter<>(GuiPilot::getModalDialogTimeout);

	private final Consumer<List<String>> dialogBoxNotHandled;

	private GuiPilot pilot;

	private final List<String> errors = new ArrayList<>();

	private PollingResult foundHandledDialog = null;

	private boolean enabled = false;

	private final Semaphore running = new Semaphore(1);


	public ModalDialogDetector(final Supplier<List<PollingResult>> pollingHandlers, final Consumer<List<String>> errorsHandler) {
		this.pollingHandlers = pollingHandlers;
		this.dialogBoxNotHandled = errorsHandler;
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
			for (final PollingResult checked : pollingHandlers.get()) {
				PollingResult result = checked;
				if (!result.handled) {
					errors.add("Unhandled dialog box: " + checked.extraInfo);
					continue;
				}
				foundHandledDialog = result;
				if (result.error == null) {
					return;
				}
				// handle error
				errors.add(result.error);
				if (result.closeOnErrorFunction != null) {
					result.closeOnErrorFunction.run();
				}

			}
			if (!errors.isEmpty()) {			
				dialogBoxNotHandled.accept(errors);
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
		Assertions.assertNull(foundHandledDialog, () -> "Detector already detected a dialog");
		if (!enabled) {
			enabled = true;
			t.schedule(timerTask(), 0, 500);
		}
		return this::close;
	}

	public synchronized PollingResult getPollingResult(Poller poller) {
		return foundHandledDialog;
	}

	public boolean waitModalDialogHandled(final FailureHandler<ModalDialogDetector.PollingResult, Boolean> onFail) {
		Poller poller = new Poller(timeout.get(), Duration.ofMillis(100), p -> Duration.ofMillis(100));
		PollingResult result = poller.run(this::getPollingResult, Objects::nonNull);
		if (result != null) {
			return true;
		}
		return onFail.apply(PollingResults.failure("Modal dialog not detected"), pilot);
	}

}
