package ch.skymarshall.dataflowmgr.examples.simple;

import ch.skymarshall.dataflowmgr.annotations.ExternalAdapters;
import ch.skymarshall.dataflowmgr.examples.simple.dto.MyData;

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
		assert result != null;
		FlowReport.add("display");
		this.output = result.output;
	}

	public String getOutput() {
		return output;
	}
}
