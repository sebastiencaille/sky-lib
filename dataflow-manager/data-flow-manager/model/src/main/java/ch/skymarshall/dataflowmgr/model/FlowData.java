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
	private UUID source;

	public FlowData(final UUID uuid) {
		super(uuid);
	}

	public void setContext(final FlowData originalData, final UUID source) {
		this.currentFlow = originalData.getCurrentFlowExecution();
		this.source = source;
	}

	public void setCurrentFlowExecution(final UUID currentFlow) {
		this.currentFlow = currentFlow;
	}

	public UUID getCurrentFlowExecution() {
		return currentFlow;
	}

	public UUID getSource() {
		return source;
	}

}
