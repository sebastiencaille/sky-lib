package ch.skymarshall.tcwriter.pilot;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;
import java.util.function.Supplier;

import org.junit.jupiter.api.Assertions;

import ch.skymarshall.util.helpers.NoExceptionCloseable;

/**
 * Handles unexpected dialog boxes, alerts, ...
 *
 * @author scaille
 *
 */
@SuppressWarnings("java:S5960")
public class ModalDialogDetector {

	public static class PollingResult {
		public final boolean handled;
		private final String error;
		private final String extraInfo;
		private Runnable closeOnErrorFunction;

		private PollingResult(final boolean handled, final String error, final Runnable closeOnErrorFunction,
				final String extraInfo) {
			this.handled = handled;
			this.error = error;
			this.closeOnErrorFunction = closeOnErrorFunction;
			this.extraInfo = extraInfo;
		}

	}

	private static final Timer timer = new Timer("Modal dialog detector");

	private Supplier<List<PollingResult>> pollingHandlers;

	private final List<String> errors = new ArrayList<>();

	private PollingResult foundHandledDialog = null;

	private boolean enabled = false;

	private Semaphore running = new Semaphore(1);

	public ModalDialogDetector(final Supplier<List<PollingResult>> pollingHandlers) {
		this.pollingHandlers = pollingHandlers;
	}

	public static PollingResult expected() {
		return new PollingResult(true, null, null, null);
	}

	public static PollingResult error(final String error, final Runnable closeFunction) {
		return new PollingResult(true, error, closeFunction, null);
	}

	public static PollingResult notHandled(final String extraInfo) {
		return new PollingResult(false, null, null, extraInfo);
	}

	private synchronized void handleModalDialogs() {
		try {
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

	private synchronized NoExceptionCloseable schedule(final Timer t) {
		Assertions.assertNull(foundHandledDialog, () -> "Detector already detected a dialog");
		if (!enabled) {
			enabled = true;
			t.schedule(timerTask(), 0, 500);
		}
		return this::close;
	}

	public synchronized PollingResult getCheckResult() {
		return foundHandledDialog;
	}

	public static NoExceptionCloseable withModalDialogDetection(final ModalDialogDetector modalDialogDetector) {
		return modalDialogDetector.schedule(timer);
	}

	public static ModalDialogDetector noDetection() {
		return new ModalDialogDetector(null) {
			@SuppressWarnings("unused")
			private void schedule() {
				// noop
			}
		};
	}

}
