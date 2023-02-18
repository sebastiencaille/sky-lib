package ch.scaille.dataflowmgr.examples.simple;

import ch.scaille.util.helpers.Logs;

public class AbstractFlow {
	protected final SimpleService simpleService = new SimpleService();
	protected final SimpleFlowConditions simpleFlowConditions = new SimpleFlowConditions();
	protected final SimpleExternalAdapter simpleExternalAdapter = new SimpleExternalAdapter();

	protected void info(String message) {
		Logs.of(this).info(message);
	}
}
