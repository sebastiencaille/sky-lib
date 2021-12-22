package ch.scaille.dataflowmgr.examples.simple;

import ch.scaille.dataflowmgr.annotations.ExternalAdapters;
import ch.scaille.dataflowmgr.examples.simple.dto.MyData;

@ExternalAdapters
public class SimpleExternalAdapter {

	private String output;

	public void reset() {
		output = null;
	}

	public String getCompletion(final MyData input) {
		FlowReport.add("getCompletion");
		switch (input.parameter) {
		case "Hello":
			return "World";
		case "Hi":
			return "There";
		default:
			throw new IllegalStateException("Unkown id: " + input);
		}
	}

	public void display(final MyData result) {
		if (result == null) {
			throw new IllegalStateException("Result must not be null");
		}
		FlowReport.add("display");
		this.output = result.output;
	}

	public String getOutput() {
		return output;
	}
}
