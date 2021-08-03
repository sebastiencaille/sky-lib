package ch.skymarshall.tcwriter.it.api;

import java.util.function.BiConsumer;

import ch.skymarshall.tcwriter.annotations.TCApi;
import ch.skymarshall.tcwriter.it.TCGuiPilot;
import ch.skymarshall.tcwriter.pilot.swing.JFileDialogPilot;

@TCApi(description = "Main frame actions", humanReadable = "Main frame actions")
public class MainFrameAction {

	private final String guiButtonName;
	private final BiConsumer<TCGuiPilot, Runnable> dialogHandler;

	public MainFrameAction(final String buttonName, BiConsumer<TCGuiPilot, Runnable> dialogHandler) {
		this.guiButtonName = buttonName;
		this.dialogHandler = dialogHandler;
	}

	public void execute(final TCGuiPilot guiPilot) {
		Runnable click = () -> guiPilot.button(guiButtonName).click();
		if (dialogHandler != null) {
			dialogHandler.accept(guiPilot, click);
		} else {
			click.run();
		}
	}

	@TCApi(description = "Load a test case", humanReadable = "load the test %s")
	public static MainFrameAction loadTC(final String testName) {
		return new MainFrameAction("LoadTC", (g, r) -> JFileDialogPilot.openFile(g).execute(r, testName));
	}

	@TCApi(description = "Save a test case", humanReadable = "save the test %s")
	public static MainFrameAction saveTC(final String testName) {
		return new MainFrameAction("SaveTC", (g, r) -> JFileDialogPilot.saveFile(g).execute(r, testName));
	}

	@TCApi(description = "Create a test case", humanReadable = "create the test %s")
	public static MainFrameAction newTC() {
		return new MainFrameAction("NewTC", null);
	}

}
