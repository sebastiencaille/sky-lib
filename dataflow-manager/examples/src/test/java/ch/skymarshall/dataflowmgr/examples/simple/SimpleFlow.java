// File generated from template
package ch.skymarshall.dataflowmgr.examples.simple;

import java.util.UUID;


public class SimpleFlow extends AbstractFlow {

	public void execute(java.lang.String inputDataPoint) {
		// ------------------------- simpleService.init -> simpleService_init -------------------------
		ch.skymarshall.dataflowmgr.examples.simple.dto.MyData simpleService_init = simpleService.init(inputDataPoint);
		
		// ------------------------- simpleService.enhance -> enhanced -------------------------
		boolean activated_a6420748_b0b3_47c6_b141_d3a2531766d3 = true;
		if (activated_a6420748_b0b3_47c6_b141_d3a2531766d3) {
		    activated_a6420748_b0b3_47c6_b141_d3a2531766d3 &= simpleServiceConditions.isEnhanceEnabled(simpleService_init);
		}
		ch.skymarshall.dataflowmgr.examples.simple.dto.MyData enhanced = null;
		if (activated_a6420748_b0b3_47c6_b141_d3a2531766d3)  {
		    String simpleExternalAdapter_enhancementa6420748_b0b3_47c6_b141_d3a2531766d3 = simpleExternalAdapter.enhancement(simpleService_init);
		    enhanced = simpleService.enhance(simpleService_init, simpleExternalAdapter_enhancementa6420748_b0b3_47c6_b141_d3a2531766d3);
		}
		
		// ------------------------- simpleService.noEnhance -> enhanced -------------------------
		boolean notExcl_enhanced = enhanced == null;
		if (notExcl_enhanced)  {
		    enhanced = simpleService.noEnhance(simpleService_init);
		}
		
		// ------------------------- exit -> exit -------------------------
		if (enhanced != null)  {
		    simpleExternalAdapter.display(enhanced);
		}
		
		
	}
	
	public static void main(String[] args) {
		new SimpleFlow().execute("Hello");
		new SimpleFlow().execute("Hi");
		new SimpleFlow().execute("Huh");
	}

}