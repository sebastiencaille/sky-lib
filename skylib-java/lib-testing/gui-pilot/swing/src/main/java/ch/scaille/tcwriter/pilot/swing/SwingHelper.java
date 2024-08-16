package ch.scaille.tcwriter.pilot.swing;

import java.awt.event.KeyEvent;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import org.junit.jupiter.api.Assertions;

@SuppressWarnings("java:S5960")
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
			Assertions.fail("Test case interrupted");
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
