package ch.scaille.tcwriter.pilot.swing;

import java.util.Locale;

import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.UIManager;

import ch.scaille.testing.testpilot.ModalDialogDetector;

public class JFileDialogPilot {

	public static String fromLocale(String buttonNameRef) {
		return UIManager.getString(buttonNameRef, Locale.getDefault());
	}

	public static JFileDialogPilot openFile(SwingPilot swingPilot) {
		return new JFileDialogPilot(swingPilot, fromLocale("FileChooser.openButtonText"));
	}

	public static JFileDialogPilot saveFile(SwingPilot swingPilot) {
		return new JFileDialogPilot(swingPilot, fromLocale("FileChooser.saveButtonText"));
	}

	protected final SwingPilot swingPilot;
	protected final String closeButtonName;

	public JFileDialogPilot(SwingPilot swingPilot, String closeButtonName) {
		this.swingPilot = swingPilot;
		this.closeButtonName = closeButtonName;
	}

	public void execute(Runnable triggerDialogAction, final String fileName) {
		swingPilot.expectModalDialog(d -> fileDialogBoxHandler(d, fileName));
		triggerDialogAction.run();
		swingPilot.waitModalDialogHandled();
	}

	public ModalDialogDetector.PollingResult fileDialogBoxHandler(final SwingModalDialogDetector dialogBox,
			String fileName) {
		final var filenameEditor = dialogBox.search(JTextField.class);
		if (filenameEditor.isEmpty()) {
			return ModalDialogDetector.notHandled("not found");
		}
		filenameEditor.get().setText(fileName);
		dialogBox.search(JButton.class, b -> closeButtonName.equals(b.getText()))
				.orElseThrow(() -> new IllegalArgumentException("No button " + closeButtonName)).doClick();
		return ModalDialogDetector.expected();
	}

}
