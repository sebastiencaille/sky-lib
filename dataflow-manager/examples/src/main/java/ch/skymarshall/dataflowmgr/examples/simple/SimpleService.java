package ch.skymarshall.dataflowmgr.examples.simple;

import ch.skymarshall.dataflowmgr.annotations.Processor;

@Processor
public class SimpleService {

	public String init(final String input) {
		return input + " -> Init";
	}

	public boolean isEnhance1Enabled(final String input) {
		return input.equals("Hello");
	}

	public String enhance1(final String input) {
		return input + " -> enhance1";
	}

	public String enhance2(final String input) {
		return input + " -> enhance2";
	}

	public void display(final String result) {
		System.out.println(result);
	}

}
