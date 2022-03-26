// File generated from template 2022/03/26 12:17:44
package ch.scaille.dataflowmgr.examples.simple;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;



class SimpleFlow extends AbstractFlow {

	public void execute(java.lang.String inputDataPoint) {
		// ------------------------- inputDataPoint -> simpleService.init -> simpleService_init -------------------------
		ch.scaille.dataflowmgr.examples.simple.dto.MyData simpleService_init = this.simpleService.init(inputDataPoint);
		
		// ------------------------- simpleService_init -> simpleService.complete -> complete -------------------------
		boolean activated_simpleService_complete_complete = true;
		if (activated_simpleService_complete_complete) {
		    activated_simpleService_complete_complete &= this.simpleFlowConditions.mustComplete(simpleService_init);
		}
		ch.scaille.dataflowmgr.examples.simple.dto.MyData complete = null;
		boolean complete_available = false;
		if (activated_simpleService_complete_complete) {
		    String simpleExternalAdapter_getCompletionsimpleService_complete_complete = this.simpleExternalAdapter.getCompletion(simpleService_init);
		    complete = this.simpleService.complete(simpleService_init,simpleExternalAdapter_getCompletionsimpleService_complete_complete);
		    complete_available = true;
		}
		
		// ------------------------- simpleService_init -> simpleService.keepAsIs -> complete -------------------------
		boolean complete_executeDefault = !complete_available;
		
		if (complete_executeDefault) {
		    complete = this.simpleService.keepAsIs(simpleService_init);
		    complete_available = true;
		}
		
		// ------------------------- complete -> exit -> exit -------------------------
		this.simpleExternalAdapter.display(complete);
		
		
	}

	@Test	
	void testFlow() {
		simpleExternalAdapter.reset();
		
	 	execute("Hello");
		Assertions.assertEquals("Hello -> complete with World", simpleExternalAdapter.getOutput());
		
		execute("Hi");
		Assertions.assertEquals("Hi -> complete with There", simpleExternalAdapter.getOutput());
		
		execute("Huh");
		Assertions.assertEquals("Huh -> keep as is", simpleExternalAdapter.getOutput());
	}
}