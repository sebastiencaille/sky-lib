// File generated from template
package ch.skymarshall.dataflowmgr.examples.simple;

import java.util.UUID;


public class SimpleFlow extends AbstractFlow {

	public void execute(java.lang.String input) {
		// ---------------- entryProcessor ----------------
		ch.skymarshall.dataflowmgr.examples.simple.SimpleService.MyData entryProcessor = simpleService.init(input);
		
		// ---------------- enhance ----------------
		ch.skymarshall.dataflowmgr.examples.simple.SimpleService.MyData enhance = null;
		if (simpleService.isEnhanceEnabled(entryProcessor))  {
		    String loadData = simpleExternalAdapter.load(entryProcessor);
		    enhance = simpleService.enhance(entryProcessor, loadData);
		}
		
		// ---------------- exitProcessor ----------------
		if (enhance != null)  {
		    simpleExternalAdapter.display(enhance);
		}
		
		// ---------------- noEnhance ----------------
		ch.skymarshall.dataflowmgr.examples.simple.SimpleService.MyData noEnhance = null;
		boolean notExcl_noEnhance = enhance == null;
		if (notExcl_noEnhance)  {
		    noEnhance = simpleService.noEnhance(entryProcessor);
		}
		
		// ---------------- exitProcessor ----------------
		if (noEnhance != null)  {
		    simpleExternalAdapter.display(noEnhance);
		}
		
		
	}
	
	public static void main(String[] args) {
		new SimpleFlow().execute("Hello");
		new SimpleFlow().execute("Hi");
		new SimpleFlow().execute("Huh");
	}

}