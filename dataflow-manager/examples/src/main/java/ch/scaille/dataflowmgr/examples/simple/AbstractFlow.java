package ch.scaille.dataflowmgr.examples.simple;

import ch.scaille.util.helpers.Logs;

public class AbstractFlow {
	protected SimpleService simpleService = new SimpleService();
	protected SimpleFlowConditions simpleFlowConditions = new SimpleFlowConditions();
	protected SimpleExternalAdapter simpleExternalAdapter = new SimpleExternalAdapter();

	protected void info(String message) {
		Logs.of(this).info(message);
	}
}
