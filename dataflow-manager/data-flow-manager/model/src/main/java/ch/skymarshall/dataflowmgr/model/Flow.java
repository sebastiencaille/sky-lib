package ch.skymarshall.dataflowmgr.model;

import java.util.UUID;

public class Flow<InputDataType extends FlowData> extends IDData {

	private final DecisionPoint<InputDataType, ?> entryPoint;

	public Flow(final UUID uuid, final DecisionPoint<InputDataType, ?> entryPoint) {
		super(uuid);
		this.entryPoint = entryPoint;
	}

	public DecisionPoint<InputDataType, ?> getEntryPoint() {
		return entryPoint;
	}

}
