package ch.skymarshall.dataflowmgr.model;

import java.util.UUID;

public class Flow<InputDataType extends FlowData> extends IDData {

	private final ActionPoint<InputDataType, ?> entryPoint;

	public Flow(final UUID uuid, final ActionPoint<InputDataType, ?> entryPoint) {
		super(uuid);
		this.entryPoint = entryPoint;
	}

	public ActionPoint<InputDataType, ?> getEntryPoint() {
		return entryPoint;
	}

}
