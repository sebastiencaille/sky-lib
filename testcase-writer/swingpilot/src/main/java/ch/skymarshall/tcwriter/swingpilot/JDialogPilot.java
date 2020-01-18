package ch.skymarshall.tcwriter.swingpilot;

import java.awt.Window;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Predicate;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import org.junit.Assert;

import ch.skymarshall.util.helpers.NoExceptionCloseable;

public class JDialogPilot extends GuiPilot {

	private static final Timer timer = new Timer();

	private final JDialog dialog;

	public JDialogPilot(final JDialog dialog) {
		super(dialog);
		this.dialog = dialog;
	}

	public JDialog getDialog() {
		return dialog;
	}

	private static class DialogBoxHandler extends TimerTask {
		private final Predicate<JDialogPilot> dialogHandler;
		private final List<String> errors = new ArrayList<>();

		public DialogBoxHandler(final Predicate<JDialogPilot> handler) {
			this.dialogHandler = handler;
		}

		@Override
		public void run() {
			SwingUtilities.invokeLater(() -> {
				for (final Window window : Window.getWindows()) {
					if (window instanceof JDialog) {
						final JDialogPilot dialogPilot = new JDialogPilot((JDialog) window);
						if (dialogHandler == null || !dialogHandler.test(dialogPilot)) {
							final Optional<JLabel> found = dialogPilot.search(JLabel.class);
							if (!found.isPresent()) {
								continue;
							}
							errors.add(found.get().getText());
							dialogPilot.getDialog().setVisible(false);
							dialogPilot.getDialog().dispose();
						}
					}
				}
			});
		}

		public void close() {
			this.cancel();
			Assert.assertEquals("Unexpected dialog boxes", "", String.join(",\n", errors));
		}

	}

	public static NoExceptionCloseable withDialogCloser() {
		final DialogBoxHandler handler = new DialogBoxHandler(guiPilot -> false);
		timer.schedule(handler, 500, 500);
		return handler::close;
	}

	public static NoExceptionCloseable withDialog(final Predicate<JDialogPilot> dialogHandler) {
		final DialogBoxHandler handler = new DialogBoxHandler(dialogHandler);
		timer.schedule(handler, 500, 500);
		return handler::close;
	}

}
