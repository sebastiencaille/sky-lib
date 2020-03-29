// File generated from template
package ch.skymarshall.dataflowmgr.examples.simple;

import org.junit.Test;
import org.junit.Assert;


public class SimpleFlow extends AbstractFlow {

	public void execute(java.lang.String inputDataPoint) {
		// ------------------------- simpleService.init -> simpleService_init -------------------------
		ch.skymarshall.dataflowmgr.examples.simple.dto.MyData simpleService_init = simpleService.init(inputDataPoint);
		
		// ------------------------- simpleService.enhance -> enhanced -------------------------
		boolean activated_a31cfec0_41e1_403e_a01d_53c98f1c49b8 = true;
		if (activated_a31cfec0_41e1_403e_a01d_53c98f1c49b8) {
		    activated_a31cfec0_41e1_403e_a01d_53c98f1c49b8 &= simpleServiceConditions.isEnhanceEnabled(simpleService_init);
		}
		ch.skymarshall.dataflowmgr.examples.simple.dto.MyData enhanced = null;
		boolean executed_enhanced = false;
		if (activated_a31cfec0_41e1_403e_a01d_53c98f1c49b8)  {
		    String simpleExternalAdapter_enhancementa31cfec0_41e1_403e_a01d_53c98f1c49b8 = simpleExternalAdapter.enhancement(simpleService_init);
		    enhanced = simpleService.enhance(simpleService_init, simpleExternalAdapter_enhancementa31cfec0_41e1_403e_a01d_53c98f1c49b8);
		    executed_enhanced = true;
		}
		
		// ------------------------- simpleService.noEnhance -> enhanced -------------------------
		boolean notExcl_enhanced = enhanced == null;
		if (notExcl_enhanced)  {
		    enhanced = simpleService.noEnhance(simpleService_init);
		    executed_enhanced = true;
		}
		
		// ------------------------- exit -> exit -------------------------
		if (executed_enhanced)  {
		    simpleExternalAdapter.display(enhanced);
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