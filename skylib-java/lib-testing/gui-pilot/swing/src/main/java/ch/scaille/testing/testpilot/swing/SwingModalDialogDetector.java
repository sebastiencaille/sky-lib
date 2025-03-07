package ch.scaille.testing.testpilot.swing;

import java.awt.Window;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.swing.JDialog;
import javax.swing.JLabel;

import ch.scaille.testing.testpilot.ModalDialogDetector;
import ch.scaille.testing.testpilot.ModalDialogDetector.PollingResult;

public class SwingModalDialogDetector extends SwingPilot {

	public static ModalDialogDetector.Builder withHandler(
			final Function<SwingModalDialogDetector, PollingResult>... pollingHandlers) {
		final var testThread = Thread.currentThread();
		return new ModalDialogDetector.Builder(() -> listDialogs(pollingHandlers), e -> testThread.interrupt());
	}

	public static ModalDialogDetector.Builder defaultDetector() {
		final var testThread = Thread.currentThread();
		return new ModalDialogDetector.Builder(() -> listDialogs(SwingModalDialogDetector::defaultCheck),
				e -> testThread.interrupt());
	}

	/**
	 * Lists all the dialogs and apply the handlers.
	 */
	public static List<ModalDialogDetector.PollingResult> listDialogs(
			final Function<SwingModalDialogDetector, PollingResult>... pollingHandlers) {
		final var result = new ArrayList<ModalDialogDetector.PollingResult>();
		SwingHelper.invokeAndWait(() -> {
			for (final var window : Window.getWindows()) {
				if (!window.isVisible() || !(window instanceof JDialog)) {
					continue;
				}
				final var dialogPilot = new SwingModalDialogDetector((JDialog) window);
				result.addAll(
						Arrays.stream(pollingHandlers).map(p -> p.apply(dialogPilot)).collect(Collectors.toList()));
			}
		});
		return result;
	}

	private final JDialog dialog;

	public SwingModalDialogDetector(final JDialog dialog) {
		super(dialog);
		this.dialog = dialog;
	}

	public JDialog getDialog() {
		return dialog;
	}

	/**
	 * Triggers an error when a dialog box is detected
	 */
	public PollingResult defaultCheck() {
		final var dialogLabel = search(JLabel.class);
		if (dialogLabel.isEmpty()) {
			return failure(dialog.getTitle());
		}
		return failure(dialogLabel.get().getText());
	}

	public void closeDialog() {
		SwingHelper.invokeAndWait(() -> {
			getDialog().setVisible(false);
			getDialog().dispose();
		});
	}

	/**
	 * Closes the dialog and trigger a failure
	 */
	public PollingResult failure(final String error) {
		return ModalDialogDetector.unhandled(error, this::closeDialog);
	}

}
