package ch.skymarshall.tcwriter.it.api;

import java.security.InvalidParameterException;
import java.util.Optional;

import javax.swing.JButton;
import javax.swing.JTextField;

import ch.skymarshall.tcwriter.annotations.TCApi;
import ch.skymarshall.tcwriter.it.TCGuiPilot;
import ch.skymarshall.tcwriter.swingpilot.JDialogPilot;

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
		guiPilot.getComponent(guiButtonName, JButton.class).doClick();
	}

	public boolean handleJDialog(final JDialogPilot jDialogPilot) {
		if (dialogCloserName == null) {
			return false;
		}

		final Optional<JTextField> filenameEditor = jDialogPilot.search(JTextField.class);
		if (!filenameEditor.isPresent()) {
			return false;
		}
		filenameEditor.get().setText(fileName);
		jDialogPilot.search(JButton.class, b -> dialogCloserName.equals(b.getText()))
				.orElseThrow(() -> new InvalidParameterException("No button " + dialogCloserName)).doClick();
		return true;
	}

	@TCApi(description = "Load a test case", humanReadable = "load the test %s")
	public static MainFrameAction loadTC(final String testName) {
		return new MainFrameAction("LoadTC", testName, "Open");
	}

	@TCApi(description = "Save a test case", humanReadable = "save the test %s")
	public static MainFrameAction saveTC(final String testName) {
		return new MainFrameAction("SaveTC", testName, "Save");
	}

	@TCApi(description = "Create a test case", humanReadable = "create the test %s")
	public static MainFrameAction newTC() {
		return new MainFrameAction("NewTC", null, null);
	}

}
