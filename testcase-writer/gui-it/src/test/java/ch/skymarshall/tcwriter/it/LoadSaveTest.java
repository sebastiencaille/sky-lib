package ch.skymarshall.tcwriter.it;

import org.junit.Test;

import ch.skymarshall.tcwriter.it.api.MainFrameAction;

public class LoadSaveTest extends AbstractGuiTest {

	@Test
	public void testSaveNewLoad() {
		testSession.injectBasicTest();
		tcWriter.mainFrameAction(MainFrameAction.saveTC("test"));
		tcWriter.mainFrameAction(MainFrameAction.newTC());
		tcWriter.mainFrameAction(MainFrameAction.loadTC("test"));
		testSession.checkBasicTest();
	}

}
