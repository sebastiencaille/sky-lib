package ch.skymarshall.dataflowmgr.engine.examples.dto;

import ch.skymarshall.dataflowmgr.model.FlowData;
import java.util.UUID;

public class IntTransfer extends FlowData {

		public IntTransfer() {
			super(null);
		}

		public IntTransfer(final UUID uuid) {
			super(uuid);
		}

		int intValue;

		public int getIntValue() {
			return intValue;
		}

		public void setIntValue(int intValue) {
			this.intValue = intValue;
		}
	
	}