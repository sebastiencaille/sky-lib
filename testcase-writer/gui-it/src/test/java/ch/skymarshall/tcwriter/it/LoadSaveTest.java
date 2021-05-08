package ch.skymarshall.tcwriter.it;

import org.junit.jupiter.api.Test;

import ch.skymarshall.tcwriter.it.api.MainFrameAction;

public class LoadSaveTest extends AbstractGuiTest {

	@Test
	public void testSaveNewLoad() {
		testSession.injectBasicTest();
		tcWriter.mainFrameAction(MainFrameAction.saveTC("test"));
		testSession.checkBasicTest();
		tcWriter.mainFrameAction(MainFrameAction.newTC());
		tcWriter.mainFrameAction(MainFrameAction.loadTC("test"));
		testSession.checkBasicTest();
	}

}
