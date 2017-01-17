package ch.skymarshall.dataflowmgr.engine.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import ch.skymarshall.dataflowmgr.model.Registry;

public class ExecutionReport {

	public enum Event {
		START_FLOW, EXECUTE_DP, DP_FINISHED, SELECT_RULES, ERROR, SELECTED_RULE, DP_NOT_READY, STOP_RULE
	}

	private final List<String> report = new ArrayList<>(20);
	private final Registry registry;

	public ExecutionReport(final Registry registry) {
		this.registry = registry;
	}

	public void add(final Event event, final UUID uuid, final UUID flowId) {
		report.add(event.name() + ": " + uuid + "/" + registry.getNameOf(uuid) + "(flow=" + flowId + ")");

	}

	public List<String> getReport() {
		return report;
	}

	public String simpleFormat() {
		final StringBuilder sb = new StringBuilder();
		for (final String str : report) {
			sb.append(str).append("\n");
		}
		return sb.toString();
	}

}
