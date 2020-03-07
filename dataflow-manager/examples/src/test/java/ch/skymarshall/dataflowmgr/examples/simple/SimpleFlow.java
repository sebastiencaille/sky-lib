// File generated from template
package ch.skymarshall.dataflowmgr.examples.simple;

import java.util.UUID;
import java.lang.String;

public class SimpleFlow extends AbstractFlow {

	public void execute(java.lang.String input) {
		java.lang.String entryProcessor = simpleService.init(input);
		java.lang.String enhance1 = simpleService.enhance1(entryProcessor);
		java.lang.String enhance2 = simpleService.enhance2(entryProcessor);
		simpleService.display(enhance2);
		simpleService.display(enhance1);
		
	}
	
	public static void main(String[] args) {
		new SimpleFlow().execute("Hello");
	}

}