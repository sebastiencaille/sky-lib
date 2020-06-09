package ch.skymarshall.dataflowmgr.examples.simple;

import ch.skymarshall.dataflowmgr.annotations.ExternalAdapters;
import ch.skymarshall.dataflowmgr.examples.simple.FlowReport.ReportEntry;
import ch.skymarshall.dataflowmgr.examples.simple.dto.MyData;

@ExternalAdapters
public class SimpleExternalAdapter {

	private String output;

	public void reset() {
		output = null;
	}

	public String enhancement(final MyData input) {
		FlowReport.report.add(new ReportEntry("enhancement"));
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
		FlowReport.report.add(new ReportEntry("display"));
		this.output = result.output;
	}

	public String getOutput() {
		return output;
	}
}
