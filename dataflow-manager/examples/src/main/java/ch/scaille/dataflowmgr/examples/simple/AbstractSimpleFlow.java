package ch.scaille.dataflowmgr.examples.simple;

import ch.scaille.dataflowmgr.runtime.rx.AbstractFlow;
import ch.scaille.util.helpers.Logs;

public class AbstractSimpleFlow extends AbstractFlow {
	protected final SimpleService simpleService = new SimpleService();
	protected final SimpleFlowConditions simpleFlowConditions = new SimpleFlowConditions();
	protected final SimpleExternalAdapter simpleExternalAdapter = new SimpleExternalAdapter();

}
