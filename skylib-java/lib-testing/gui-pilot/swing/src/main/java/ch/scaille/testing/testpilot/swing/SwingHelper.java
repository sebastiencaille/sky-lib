package ch.scaille.testing.testpilot.swing;

import java.awt.event.KeyEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.function.Supplier;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

public class SwingHelper {

	private SwingHelper() {
		// nop
	}

	/**
	 * Low level calls to Swing. Prefer withSwing methods
	 *
     */
	public static void invokeAndWait(final Runnable runnable) {
		try {
			SwingUtilities.invokeAndWait(runnable);
		} catch (final InvocationTargetException e) {
			throw new AssertionError(e.getCause());
		} catch (final InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new AssertionError("Test case interrupted");
		}
	}
	
	/**
	 * Low level calls to Swing. Prefer withSwing methods
	 *
     */
	public static <T> T invokeAndWait(final Supplier<T> runnable) {
		try {
			final var response = new Object[1];
			SwingUtilities.invokeAndWait(() -> response[0] = runnable.get());
			return (T) response[0];
		} catch (final InvocationTargetException e) {
			throw new AssertionError(e.getCause());
		} catch (final InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new AssertionError("Test case interrupted");
		}
	}

	public static void checkSwingThread() {
		if (!SwingUtilities.isEventDispatchThread()) {
			throw new IllegalStateException("Not in Swing thread");
		}
	}

	public static void doPressReturn(final JComponent t) {
		SwingHelper.checkSwingThread();
		t.dispatchEvent(new KeyEvent(t, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, KeyEvent.VK_ENTER, '\n'));
	}
}
