// File generated from template
package ch.skymarshall.dataflowmgr.examples.simple;

import org.junit.Test;
import org.junit.Assert;


public class SimpleFlow extends AbstractFlow {

	public void execute(java.lang.String inputDataPoint) {
		// ------------------------- inputDataPoint -> simpleService.init -> simpleService_init -------------------------
		ch.skymarshall.dataflowmgr.examples.simple.dto.MyData simpleService_init = this.simpleService.init(inputDataPoint);
		
		// ------------------------- simpleService_init -> simpleService.enhance -> enhanced -------------------------
		boolean activated_b4e5f882_84c0_476d_beaf_dd5a740ec0ec = true;
		if (activated_b4e5f882_84c0_476d_beaf_dd5a740ec0ec) {
		    activated_b4e5f882_84c0_476d_beaf_dd5a740ec0ec &= this.simpleServiceConditions.isEnhanceEnabled(simpleService_init);
		}
		ch.skymarshall.dataflowmgr.examples.simple.dto.MyData enhanced = null;
		boolean enhanced_available = false;
		if (activated_b4e5f882_84c0_476d_beaf_dd5a740ec0ec) {
		    String simpleExternalAdapter_enhancementb4e5f882_84c0_476d_beaf_dd5a740ec0ec = this.simpleExternalAdapter.enhancement(simpleService_init);
		    enhanced = this.simpleService.enhance(simpleService_init,simpleExternalAdapter_enhancementb4e5f882_84c0_476d_beaf_dd5a740ec0ec);
		    enhanced_available = true;
		}
		
		// ------------------------- simpleService_init -> simpleService.noEnhance -> enhanced -------------------------
		boolean enhanced_executeDefault = !enhanced_available;
		
		if (enhanced_executeDefault) {
		    enhanced = this.simpleService.noEnhance(simpleService_init);
		    enhanced_available = true;
		}
		
		// ------------------------- enhanced -> exit -> exit -------------------------
		if (enhanced_available) {
		    this.simpleExternalAdapter.display(enhanced);
		}
		
		
	}

	@Test	
	public void testFlow() {
		simpleExternalAdapter.reset();
		
	 	execute("Hello");
		Assert.assertEquals("Hello -> enhanced with World", simpleExternalAdapter.getOutput());
		
		execute("Hi");
		Assert.assertEquals("Hi -> enhanced with There", simpleExternalAdapter.getOutput());
		
		execute("Huh");
		Assert.assertEquals("Huh -> not enhanced", simpleExternalAdapter.getOutput());
	}

}