// File generated from template
package ch.skymarshall.dataflowmgr.examples.simple;

import org.junit.Test;
import org.junit.Assert;


public class SimpleFlow extends AbstractFlow {

	public void execute(java.lang.String inputDataPoint) {
		// ------------------------- inputDataPoint -> simpleService.init -> simpleService_init -------------------------
		ch.skymarshall.dataflowmgr.examples.simple.dto.MyData simpleService_init = this.simpleService.init(inputDataPoint);
		
		// ------------------------- simpleService_init -> simpleService.complete -> complete -------------------------
		boolean activated_c0f38ebe_4902_4054_85ff_5dd949ea2697 = true;
		if (activated_c0f38ebe_4902_4054_85ff_5dd949ea2697) {
		    activated_c0f38ebe_4902_4054_85ff_5dd949ea2697 &= this.simpleFlowConditions.mustComplete(simpleService_init);
		}
		ch.skymarshall.dataflowmgr.examples.simple.dto.MyData complete = null;
		boolean complete_available = false;
		if (activated_c0f38ebe_4902_4054_85ff_5dd949ea2697) {
		    String simpleExternalAdapter_getCompletionc0f38ebe_4902_4054_85ff_5dd949ea2697 = this.simpleExternalAdapter.getCompletion(simpleService_init);
		    complete = this.simpleService.complete(simpleService_init,simpleExternalAdapter_getCompletionc0f38ebe_4902_4054_85ff_5dd949ea2697);
		    complete_available = true;
		}
		
		// ------------------------- simpleService_init -> simpleService.keepAsIs -> complete -------------------------
		boolean complete_executeDefault = !complete_available;
		
		if (complete_executeDefault) {
		    complete = this.simpleService.keepAsIs(simpleService_init);
		    complete_available = true;
		}
		
		// ------------------------- complete -> exit -> exit -------------------------
		if (complete_available) {
		    this.simpleExternalAdapter.display(complete);
		}
		
		
	}

	@Test	
	public void testFlow() {
		simpleExternalAdapter.reset();
		
	 	execute("Hello");
		Assert.assertEquals("Hello -> complete with World", simpleExternalAdapter.getOutput());
		
		execute("Hi");
		Assert.assertEquals("Hi -> complete with There", simpleExternalAdapter.getOutput());
		
		execute("Huh");
		Assert.assertEquals("Huh -> keep as is", simpleExternalAdapter.getOutput());
	}
}