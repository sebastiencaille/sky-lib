package ch.scaille.dataflowmgr.examples.simple.dto;

public record MyData(String parameter, String output) {

	public MyData(final String input) {
		this(input, input);
	}

	public MyData(final MyData orig, final String flowInfo) {
		this(orig.parameter, orig.parameter + flowInfo);
	}
}