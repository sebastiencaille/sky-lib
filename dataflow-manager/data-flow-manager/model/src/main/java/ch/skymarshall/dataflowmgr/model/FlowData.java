package ch.skymarshall.dataflowmgr.model;

import java.util.UUID;

/**
 * Data received / send by decision point
 *
 * @author scaille
 *
 */
public class FlowData extends IDData {

	private UUID currentFlow;

	public FlowData(final UUID uuid, final UUID currentFlow) {
		super(uuid);
		this.currentFlow = currentFlow;
	}

	public FlowData(final UUID uuid) {
		super(uuid);
	}

	public void setCurrentFlowExecution(final UUID currentFlow) {
		this.currentFlow = currentFlow;
	}

	public UUID getCurrentFlowExecution() {
		return currentFlow;
	}

}
