package ch.skymarshall.tcwriter.it.api;

import java.util.function.BiConsumer;
import java.util.function.Function;

import ch.skymarshall.tcwriter.annotations.TCApi;
import ch.skymarshall.tcwriter.it.TCGuiPilot;
import ch.skymarshall.tcwriter.it.TCWriterPage;
import ch.skymarshall.tcwriter.pilot.swing.JButtonPilot;
import ch.skymarshall.tcwriter.pilot.swing.JFileDialogPilot;

@TCApi(description = "Main frame actions", humanReadable = "Main frame actions")
public class MainFrameAction {

	private final Function<TCWriterPage, JButtonPilot> button;
	private final BiConsumer<TCGuiPilot, Runnable> dialogHandler;

	public MainFrameAction(final Function<TCWriterPage, JButtonPilot> button,
			BiConsumer<TCGuiPilot, Runnable> dialogHandler) {
		this.button = button;
		this.dialogHandler = dialogHandler;
	}

	public void execute(TCWriterPage page, final TCGuiPilot guiPilot) {
		Runnable buttonClick = () -> button.apply(page).click();
		if (dialogHandler != null) {
			dialogHandler.accept(guiPilot, buttonClick);
		} else {
			buttonClick.run();
		}
	}

	@TCApi(description = "Load a test case", humanReadable = "load the test %s")
	public static MainFrameAction loadTC(final String testName) {
		return new MainFrameAction(b -> b.loadTC, (g, r) -> JFileDialogPilot.openFile(g).execute(r, testName));
	}

	@TCApi(description = "Save a test case", humanReadable = "save the test %s")
	public static MainFrameAction saveTC(final String testName) {
		return new MainFrameAction(b -> b.saveTC, (g, r) -> JFileDialogPilot.saveFile(g).execute(r, testName));
	}

	@TCApi(description = "Create a test case", humanReadable = "create the test %s")
	public static MainFrameAction newTC() {
		return new MainFrameAction(b -> b.newTC, null);
	}

}
