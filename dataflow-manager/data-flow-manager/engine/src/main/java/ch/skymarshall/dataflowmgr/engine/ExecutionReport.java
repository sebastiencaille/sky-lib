package ch.skymarshall.dataflowmgr.engine;

import java.util.ArrayList;
import java.util.List;

public class ExecutionReport {

	public enum Event {
		START_FLOW, PREPARE_DP, EXECUTE_DP, DP_FINISHED
	}

	private final List<String> report = new ArrayList<>(20);

	public void add(final Event event, final String name) {
		report.add(event.name() + ": " + name);
	}

	public void add(final Event event, final String name, final Object details) {
		report.add(event.name() + ": " + name + "=" + details);

	}

	public List<String> getReport() {
		return report;
	}

}
