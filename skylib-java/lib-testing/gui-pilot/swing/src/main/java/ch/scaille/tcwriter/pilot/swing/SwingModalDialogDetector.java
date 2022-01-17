package ch.scaille.tcwriter.pilot.swing;

import java.awt.Window;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.swing.JDialog;
import javax.swing.JLabel;

import ch.scaille.tcwriter.pilot.ModalDialogDetector;
import ch.scaille.tcwriter.pilot.ModalDialogDetector.PollingResult;

public class SwingModalDialogDetector extends SwingPilot {

	public static ModalDialogDetector withHandler(
			final Function<SwingModalDialogDetector, PollingResult>... pollingHandlers) {
		return new ModalDialogDetector(() -> listDialogs(pollingHandlers));
	}

	public static ModalDialogDetector defaultDetector() {
		return new ModalDialogDetector(() -> listDialogs(SwingModalDialogDetector::defaultCheck));
	}

	/**
	 * Lists all the windows and apply the handlers.
	 * 
	 * @param pollingHandler
	 * @return
	 */
	public static List<ModalDialogDetector.PollingResult> listDialogs(
			final Function<SwingModalDialogDetector, PollingResult>... pollingHandlers) {
		final List<ModalDialogDetector.PollingResult> result = new ArrayList<>();
		SwingHelper.invokeAndWait(() -> {
			for (final Window window : Window.getWindows()) {
				if (!window.isVisible() || !(window instanceof JDialog)) {
					continue;
				}
				final SwingModalDialogDetector dialogPilot = new SwingModalDialogDetector((JDialog) window);
				result.addAll(Arrays.stream(pollingHandlers).map(p -> p.apply(dialogPilot)).collect(Collectors.toList()));
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
	 * 
	 * @return
	 */
	public PollingResult defaultCheck() {
		final Optional<JLabel> dialogLabel = search(JLabel.class);
		if (!dialogLabel.isPresent()) {
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
	 * Close the dialog and trigger a failure
	 * 
	 * @param error
	 * @return
	 */
	public PollingResult failure(final String error) {
		return ModalDialogDetector.error(error, this::closeDialog);
	}

}
