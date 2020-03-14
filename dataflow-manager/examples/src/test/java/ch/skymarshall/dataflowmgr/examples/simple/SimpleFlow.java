// File generated from template
package ch.skymarshall.dataflowmgr.examples.simple;

import java.util.UUID;


public class SimpleFlow extends AbstractFlow {

	public void execute(java.lang.String input) {
		// ---------------- entryProcessor ----------------
		String entryProcessor = simpleService.init(input);
		
		// ---------------- enhance1 ----------------
		String enhance1 = null;
		if ((entryProcessor.equals("Hello")))  {
		    enhance1 = simpleService.enhance1(entryProcessor);
		}
		
		// ---------------- display ----------------
		if (enhance1 != null)  {
		    simpleService.display(enhance1);
		}
		
		// ---------------- enhance2 ----------------
		String enhance2 = null;
		boolean notExcl_enhance2 = enhance1 == null;
		if (notExcl_enhance2)  {
		    enhance2 = simpleService.enhance2(entryProcessor);
		}
		
		// ---------------- display ----------------
		if (enhance2 != null)  {
		    simpleService.display(enhance2);
		}
		
		
	}
	
	public static void main(String[] args) {
		new SimpleFlow().execute("Hello");
		new SimpleFlow().execute("Hi");
	}

}