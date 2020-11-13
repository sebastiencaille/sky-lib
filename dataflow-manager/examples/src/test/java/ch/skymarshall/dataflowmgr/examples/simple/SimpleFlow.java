// File generated from template
package ch.skymarshall.dataflowmgr.examples.simple;

import org.junit.Test;
import org.junit.Assert;


public class SimpleFlow extends AbstractFlow {

	public void execute(java.lang.String inputDataPoint) {
		// ------------------------- inputDataPoint -> simpleService.init -> simpleService_init -------------------------
		ch.skymarshall.dataflowmgr.examples.simple.dto.MyData simpleService_init = this.simpleService.init(inputDataPoint);
		
		// ------------------------- simpleService_init -> simpleService.enhance -> enhanced -------------------------
		boolean activated_481a956b_43f6_4d34_884c_5b89346d2e3b = true;
		if (activated_481a956b_43f6_4d34_884c_5b89346d2e3b) {
		    activated_481a956b_43f6_4d34_884c_5b89346d2e3b &= this.simpleServiceConditions.isEnhanceEnabled(simpleService_init);
		}
		ch.skymarshall.dataflowmgr.examples.simple.dto.MyData enhanced = null;
		boolean enhanced_available = false;
		if (activated_481a956b_43f6_4d34_884c_5b89346d2e3b) {
		    String simpleExternalAdapter_enhancement481a956b_43f6_4d34_884c_5b89346d2e3b = this.simpleExternalAdapter.enhancement(simpleService_init);
		    enhanced = this.simpleService.enhance(simpleService_init,simpleExternalAdapter_enhancement481a956b_43f6_4d34_884c_5b89346d2e3b);
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