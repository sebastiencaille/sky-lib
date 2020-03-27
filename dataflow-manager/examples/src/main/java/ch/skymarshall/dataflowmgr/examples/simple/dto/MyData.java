package ch.skymarshall.dataflowmgr.examples.simple.dto;

public class MyData {
	public final String parameter;
	public final String output;

	public MyData(final String input) {
		parameter = input;
		output = input;
	}

	public MyData(final MyData orig, final String flowInfo) {
		parameter = orig.parameter;
		output = orig.parameter + flowInfo;
	}
}