package ch.skymarshall.tcwriter.pilot;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;
import java.util.function.Supplier;

import org.junit.Assert;

import ch.skymarshall.util.helpers.NoExceptionCloseable;

/**
 * Handles unexpected dialog boxes, alerts, ...
 *
 * @author scaille
 *
 */
public class ModalDialogDetector {

	public static class ErrorCheck {
		public final boolean handled;
		private final String error;
		private final String extraInfo;
		private Runnable closeFunction;

		private ErrorCheck(final boolean handled, final String extraInfo, final String error,
				final Runnable closeFunction) {
			this.handled = handled;
			this.error = error;
			this.closeFunction = closeFunction;
			this.extraInfo = extraInfo;
		}

	}

	private static final Timer timer = new Timer("Modal dialog detector");

	private Supplier<List<ErrorCheck>> errorChecks;

	private final List<String> errors = new ArrayList<>();

	private ErrorCheck foundDialog = null;

	private boolean enabled = false;

	private Semaphore running = new Semaphore(1);

	public ModalDialogDetector(final Supplier<List<ErrorCheck>> errorChecks) {
		this.errorChecks = errorChecks;
	}

	public static ErrorCheck ignore() {
		return new ErrorCheck(true, null, null, null);
	}

	public static ErrorCheck error(final String error, final Runnable closeFunction) {
		return new ErrorCheck(true, null, error, closeFunction);
	}

	public static ErrorCheck fallback(final String extraInfo) {
		return new ErrorCheck(false, extraInfo, null, null);
	}

	private synchronized void handleModalDialogs() {
		try {
			running.acquire();
			for (final ErrorCheck checked : errorChecks.get()) {
				ErrorCheck result = checked;
				if (!result.handled) {
					result = error("Unhandled fallback: " + checked.extraInfo, null);
				} else {
					foundDialog = result;
				}
				if (result.error == null) {
					return;
				}
				errors.add(result.error);
				if (result.closeFunction != null) {
					result.closeFunction.run();
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
		Assert.assertEquals("Unexpected modal dialog", "", String.join(",\n", errors));
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
			Assert.fail(e.getMessage());
		}
	}

	private synchronized NoExceptionCloseable schedule(final Timer t) {
		Assert.assertNull("Detector already detected a dialog", foundDialog);
		if (!enabled) {
			enabled = true;
			t.schedule(timerTask(), 0, 500);
		}
		return this::close;
	}

	public synchronized ErrorCheck getCheckResult() {
		return foundDialog;
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
