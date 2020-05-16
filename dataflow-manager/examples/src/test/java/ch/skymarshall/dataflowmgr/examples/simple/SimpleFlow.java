// File generated from template
package ch.skymarshall.dataflowmgr.examples.simple;

import org.junit.Test;
import org.junit.Assert;


public class SimpleFlow extends AbstractFlow {

	public void execute(java.lang.String inputDataPoint) {
		// ------------------------- simpleService.init -> simpleService_init -------------------------
		ch.skymarshall.dataflowmgr.examples.simple.dto.MyData simpleService_init = simpleService.init(inputDataPoint);
		
		// ------------------------- simpleService.enhance -> enhanced -------------------------
		boolean activated_728541a9_5503_4c37_9624_7c0bc58293a8 = true;
		if (activated_728541a9_5503_4c37_9624_7c0bc58293a8) {
		    activated_728541a9_5503_4c37_9624_7c0bc58293a8 &= simpleServiceConditions.isEnhanceEnabled(simpleService_init);
		}
		ch.skymarshall.dataflowmgr.examples.simple.dto.MyData enhanced = null;
		boolean executed_enhanced = false;
		if (activated_728541a9_5503_4c37_9624_7c0bc58293a8)  {
		    String simpleExternalAdapter_enhancement728541a9_5503_4c37_9624_7c0bc58293a8 = simpleExternalAdapter.enhancement(simpleService_init);
		    enhanced = simpleService.enhance(simpleService_init, simpleExternalAdapter_enhancement728541a9_5503_4c37_9624_7c0bc58293a8);
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