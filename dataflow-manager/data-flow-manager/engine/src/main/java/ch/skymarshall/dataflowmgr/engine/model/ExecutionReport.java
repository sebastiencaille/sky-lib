/*******************************************************************************
 * Copyright (c) 2017 Sebastien Caille.
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms are permitted
 *  provided that the above copyright notice and this paragraph are
 *  duplicated in all such forms and that any documentation,
 *  advertising materials, and other materials related to such
 *  distribution and use acknowledge that the software was developed
 *  by Sebastien Caille.  The name of Sebastien Caille may not be used to endorse or promote products derived
 *  from this software without specific prior written permission.
 *  THIS SOFTWARE IS PROVIDED ``AS IS'' AND WITHOUT ANY EXPRESS OR
 *  IMPLIED WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 ******************************************************************************/
package ch.skymarshall.dataflowmgr.engine.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import ch.skymarshall.dataflowmgr.model.Registry;
import ch.skymarshall.dataflowmgr.model.Step;

public class ExecutionReport {

	public enum Event {
		START_FLOW, EXECUTE_AP, ERROR, SELECT_OUTPUT_RULES, SELECTED_OUTPUT_RULE, AP_NOT_READY, STOP_RULE, SELECT_AND_EXEC_INPUT_RULE, EXECUTED_INPUT_RULE
	}

	private final List<String> report = new ArrayList<>(20);
	private final List<Step> steps = new ArrayList<>(20);
	private final Registry registry;

	public ExecutionReport(final Registry registry) {
		this.registry = registry;
	}

	public void add(final Event event, final UUID uuid, final UUID flowId) {
		report.add(event.name() + ": " + uuid + "/" + registry.getNameOf(uuid) + "(flow=" + flowId + ")");
		if (event == Event.EXECUTE_AP || event == Event.SELECTED_OUTPUT_RULE || event == Event.EXECUTED_INPUT_RULE) {
			steps.add(new Step(flowId, uuid));
		}

	}

	public void add(final Event event, final UUID uuid, final UUID flowId, final String message) {
		report.add(
				event.name() + ": " + uuid + "/" + registry.getNameOf(uuid) + ":" + message + " (flow=" + flowId + ")");
	}

	public List<String> getReport() {
		return report;
	}

	public List<Step> getSteps() {
		return steps;
	}

	public String simpleFormat() {
		final StringBuilder sb = new StringBuilder();
		report.forEach(str -> sb.append(str).append("\n"));
		return sb.toString();
	}

}
