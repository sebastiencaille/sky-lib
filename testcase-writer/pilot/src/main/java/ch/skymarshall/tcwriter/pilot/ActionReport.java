package ch.skymarshall.tcwriter.pilot;

import java.util.ArrayList;
import java.util.List;

public class ActionReport {

	private final List<String> report = new ArrayList<>();

	public void report(final String reportLine) {
		report.add(reportLine);
	}

	public List<String> getReport() {
		return report;
	}

	public String getFormattedReport() {
		return String.join("\n", report);
	}

	@Override
	public String toString() {
		return "Report: " + report.size() + " lines";
	}

}
