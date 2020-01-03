package ch.skymarshall.tcwriter.it;

import org.junit.Test;

public class StepSelectionTest extends AbstractGuiTest {

	@Test
	public void addStep() {
		tcWriter.selectStep(StepSelector.addStep());
		tcWriter.selectStep(StepSelector.selectStep(1));
	}

}
