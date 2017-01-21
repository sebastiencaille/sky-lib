package ch.skymarshall.dataflowmgr.model;

import java.util.UUID;

public class NoData extends FlowData {
	public static NoData NO_DATA = new NoData() {
		@Override
		public void setCurrentFlowExecution(final UUID currentFlow) {
		}
	};

	public NoData() {
		super(UUIDs.NO_DATA);
	}

}
