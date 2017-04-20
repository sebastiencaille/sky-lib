package ch.skymarshall.dataflowmgr.model;

import java.util.UUID;

public class Step {
	public UUID uuid;
	public UUID flowId;

	public Step() {
		uuid = null;
		flowId = null;
	}

	public Step(final UUID flowId, final UUID uuid) {
		this.uuid = uuid;
		this.flowId = flowId;
	}

}