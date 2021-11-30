package ch.skymarshall.tcwriter.it;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import ch.skymarshall.tcwriter.it.api.MainFrameAction;
import ch.skymarshall.tcwriter.jupiter.DisabledIfHeadless;

@ExtendWith(DisabledIfHeadless.class)
class LoadSaveTest extends AbstractGuiTest {

	@Test
	void testSaveNewLoad() {
		testSession.injectBasicTest();
		tcWriter.mainFrameAction(MainFrameAction.saveTC("test"));
		testSession.checkBasicTest();
		tcWriter.mainFrameAction(MainFrameAction.newTC());
		tcWriter.mainFrameAction(MainFrameAction.loadTC("test"));
		testSession.checkBasicTest();
	}

}
