package ch.skymarshall.tcwriter.pilot.swing;

import java.awt.Window;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import javax.swing.JDialog;
import javax.swing.JLabel;

import ch.skymarshall.tcwriter.pilot.ModalDialogDetector;
import ch.skymarshall.tcwriter.pilot.ModalDialogDetector.ErrorCheck;

public class SwingModalDialogDetector extends SwingGuiPilot {

	public static List<ModalDialogDetector.ErrorCheck> listDialogs(
			final Function<SwingModalDialogDetector, ErrorCheck> errorCheck) {
		final List<ModalDialogDetector.ErrorCheck> result = new ArrayList<>();
		SwingGuiPilot.invokeAndWait(() -> {
			for (final Window window : Window.getWindows()) {
				if (!window.isVisible() || !(window instanceof JDialog)) {
					continue;
				}
				final SwingModalDialogDetector dialogPilot = new SwingModalDialogDetector((JDialog) window);
				result.add(applyCheck(errorCheck, dialogPilot));
			}
		});
		return result;
	}

	private static ErrorCheck applyCheck(final Function<SwingModalDialogDetector, ErrorCheck> errorCheck,
			final SwingModalDialogDetector dialogPilot) {
		ErrorCheck checked = ModalDialogDetector.fallback("");
		if (errorCheck != null) {
			checked = errorCheck.apply(dialogPilot);
		}
		if (checked.handled) {
			return checked;
		}
		return dialogPilot.defaultCheck();
	}

	private final JDialog dialog;

	public SwingModalDialogDetector(final JDialog dialog) {
		super(dialog);
		this.dialog = dialog;
	}

	public JDialog getDialog() {
		return dialog;
	}

	public ErrorCheck defaultCheck() {
		final Optional<JLabel> dialogLabel = search(JLabel.class);
		if (!dialogLabel.isPresent()) {
			return error("Unexpected dialog box: " + dialog.getTitle());
		}
		return error(dialogLabel.get().getText());
	}

	public void closeFunction() {
		SwingGuiPilot.invokeAndWait(() -> {
			getDialog().setVisible(false);
			getDialog().dispose();
		});
	}

	public ErrorCheck error(final String error) {
		return ModalDialogDetector.error(error, this::closeFunction);
	}

	public static ModalDialogDetector withCheck(final Function<SwingModalDialogDetector, ErrorCheck> errorCheck) {
		return new ModalDialogDetector(() -> listDialogs(errorCheck));
	}

	public static ModalDialogDetector defaultDetector() {
		return new ModalDialogDetector(() -> listDialogs(null));
	}

	public ErrorCheck fallback() {
		return ModalDialogDetector.fallback(getDialog().getTitle());
	}

}
