// File generated from template
package ch.skymarshall.dataflowmgr.examples.simple;

import org.junit.Test;
import org.junit.Assert;


public class SimpleFlow extends AbstractFlow {

	public void execute(java.lang.String inputDataPoint) {
		// ------------------------- inputDataPoint -> simpleService.init -> simpleService_init -------------------------
		ch.skymarshall.dataflowmgr.examples.simple.dto.MyData simpleService_init = this.simpleService.init(inputDataPoint);
		
		// ------------------------- simpleService_init -> simpleService.enhance -> enhanced -------------------------
		boolean activated_9872854a_fece_46e8_b8be_b700fd3fed9a = true;
		if (activated_9872854a_fece_46e8_b8be_b700fd3fed9a) {
		    activated_9872854a_fece_46e8_b8be_b700fd3fed9a &= this.simpleServiceConditions.isEnhanceEnabled(simpleService_init);
		}
		ch.skymarshall.dataflowmgr.examples.simple.dto.MyData enhanced = null;
		boolean executed_enhanced = false;
		if (activated_9872854a_fece_46e8_b8be_b700fd3fed9a) {
		    String simpleExternalAdapter_enhancement9872854a_fece_46e8_b8be_b700fd3fed9a = this.simpleExternalAdapter.enhancement(simpleService_init);
		    enhanced = this.simpleService.enhance(simpleService_init, simpleExternalAdapter_enhancement9872854a_fece_46e8_b8be_b700fd3fed9a);
		    executed_enhanced = true;
		}
		
		// ------------------------- simpleService_init -> simpleService.noEnhance -> enhanced -------------------------
		boolean executeDefault_enhanced = !executed_enhanced;
		
		if (executeDefault_enhanced) {
		    enhanced = this.simpleService.noEnhance(simpleService_init);
		    executed_enhanced = true;
		}
		
		// ------------------------- enhanced -> exit -> exit -------------------------
		if (executed_enhanced) {
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