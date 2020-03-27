// File generated from template
package ch.skymarshall.dataflowmgr.examples.simple;

import java.util.UUID;


public class SimpleFlow extends AbstractFlow {

	public void execute(String inputDataPoint) {
		// ---------------- simpleService.init -> simpleService_init ----------------
		ch.skymarshall.dataflowmgr.examples.simple.SimpleService.MyData simpleService_init = simpleService.init(inputDataPoint);
		
		// ---------------- simpleService.enhance -> enhanced ----------------
		ch.skymarshall.dataflowmgr.examples.simple.SimpleService.MyData enhanced = null;
		if (simpleService.isEnhanceEnabled(simpleService_init))  {
		    String adapter_1 = simpleExternalAdapter.load(simpleService_init);
		    enhanced = simpleService.enhance(simpleService_init, adapter_1);
		}
		
		// ---------------- exit -> exit ----------------
		if (enhanced != null)  {
		    simpleExternalAdapter.display(enhanced);
		}
		
		// ---------------- simpleService.noEnhance -> enhanced ----------------
		boolean notExcl_enhanced = enhanced == null;
		if (notExcl_enhanced)  {
		    enhanced = simpleService.noEnhance(simpleService_init);
		}
		
		
	}
	
	public static void main(String[] args) {
		new SimpleFlow().execute("Hello");
		new SimpleFlow().execute("Hi");
		new SimpleFlow().execute("Huh");
	}

}