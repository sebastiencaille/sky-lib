package ch.skymarshall.tcwriter.it.api;

import java.security.InvalidParameterException;
import java.util.Locale;
import java.util.Optional;

import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.UIManager;

import ch.skymarshall.tcwriter.annotations.TCApi;
import ch.skymarshall.tcwriter.it.TCGuiPilot;
import ch.skymarshall.tcwriter.pilot.ModalDialogDetector;
import ch.skymarshall.tcwriter.pilot.swing.SwingModalDialogDetector;

@TCApi(description = "Main frame actions", humanReadable = "Main frame actions")
public class MainFrameAction {

	private final String guiButtonName;
	private final String fileName;
	private final String dialogCloserName;

	public MainFrameAction(final String buttonName, final String fileName, final String dialogCloserName) {
		this.guiButtonName = buttonName;
		this.fileName = fileName;
		this.dialogCloserName = dialogCloserName;

	}

	public void execute(final TCGuiPilot guiPilot) {
		if (dialogCloserName != null) {
			guiPilot.expectModalDialog(this::openSaveDialogBoxHandler);
		}
		guiPilot.button(guiButtonName).click();
		if (dialogCloserName != null) {
			guiPilot.waitModalDialogHandled();
		}
	}

	public ModalDialogDetector.ErrorCheck openSaveDialogBoxHandler(final SwingModalDialogDetector errorDialogPilot) {
		if (dialogCloserName == null) {
			return errorDialogPilot.fallback();
		}

		final Optional<JTextField> filenameEditor = errorDialogPilot.search(JTextField.class);
		if (!filenameEditor.isPresent()) {
			return errorDialogPilot.fallback();
		}
		filenameEditor.get().setText(fileName);
		errorDialogPilot.search(JButton.class, b -> dialogCloserName.equals(b.getText()))
				.orElseThrow(() -> new InvalidParameterException("No button " + dialogCloserName)).doClick();
		return ModalDialogDetector.ignore();
	}

	@TCApi(description = "Load a test case", humanReadable = "load the test %s")
	public static MainFrameAction loadTC(final String testName) {
		return new MainFrameAction("LoadTC", testName, UIManager.getString("FileChooser.openButtonText", Locale.getDefault()));
	}

	@TCApi(description = "Save a test case", humanReadable = "save the test %s")
	public static MainFrameAction saveTC(final String testName) {
		return new MainFrameAction("SaveTC", testName, UIManager.getString("FileChooser.saveButtonText", Locale.getDefault()));
	}

	@TCApi(description = "Create a test case", humanReadable = "create the test %s")
	public static MainFrameAction newTC() {
		return new MainFrameAction("NewTC", null, null);
	}

}
