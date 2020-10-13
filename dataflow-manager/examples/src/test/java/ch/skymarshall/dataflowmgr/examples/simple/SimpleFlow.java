// File generated from template
package ch.skymarshall.dataflowmgr.examples.simple;

import org.junit.Test;
import org.junit.Assert;


public class SimpleFlow extends AbstractFlow {

	public void execute(java.lang.String inputDataPoint) {
		// ------------------------- inputDataPoint -> simpleService.init -> simpleService_init -------------------------
		ch.skymarshall.dataflowmgr.examples.simple.dto.MyData simpleService_init = this.simpleService.init(inputDataPoint);
		
		// ------------------------- simpleService_init -> simpleService.enhance -> enhanced -------------------------
		boolean activated_22599780_f1d3_4727_9d45_4f15a317416d = true;
		if (activated_22599780_f1d3_4727_9d45_4f15a317416d) {
		    activated_22599780_f1d3_4727_9d45_4f15a317416d &= this.simpleServiceConditions.isEnhanceEnabled(simpleService_init);
		}
		ch.skymarshall.dataflowmgr.examples.simple.dto.MyData enhanced = null;
		boolean enhanced_available = false;
		if (activated_22599780_f1d3_4727_9d45_4f15a317416d) {
		    String simpleExternalAdapter_enhancement22599780_f1d3_4727_9d45_4f15a317416d = this.simpleExternalAdapter.enhancement(simpleService_init);
		    enhanced = this.simpleService.enhance(simpleService_init,simpleExternalAdapter_enhancement22599780_f1d3_4727_9d45_4f15a317416d);
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