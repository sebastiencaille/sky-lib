package ch.skymarshall.dataflowmgr.examples.simple;

import ch.skymarshall.dataflowmgr.annotations.Processor;

@Processor
public class SimpleService {

	public static class MyData {
		protected final String parameter;
		protected final String output;

		public MyData(final String input) {
			parameter = input;
			output = input;
		}

		public MyData(final MyData orig, final String flowInfo) {
			parameter = orig.parameter;
			output = orig.parameter + flowInfo;
		}
	}

	public MyData init(final String input) {
		return new MyData(input);
	}

	public boolean isEnhanceEnabled(final MyData input) {
		return input.parameter.equals("Hello") || input.parameter.equals("Hi");
	}

	public MyData enhance(final MyData input, final String externalData) {
		return new MyData(input, " -> enhanced with " + externalData);
	}

	public MyData noEnhance(final MyData input) {
		return new MyData(input, " -> not enhanced");
	}

}
