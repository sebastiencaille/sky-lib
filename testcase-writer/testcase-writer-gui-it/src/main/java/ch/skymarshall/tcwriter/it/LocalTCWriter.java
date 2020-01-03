package ch.skymarshall.tcwriter.it;

public class LocalTCWriter implements TestWriterRole {

	private final TCGuiPilot guiPilot;

	public LocalTCWriter(final TCGuiPilot guiPilot) {
		this.guiPilot = guiPilot;
	}

	@Override
	public void selectStep(final StepSelector selector) {
		selector.select(guiPilot);
	}

}
