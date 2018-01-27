// File generated from template
// SingleNodeWriter -c /home/scaille/src/github/sky-lib/dataflow-manager/data-flow-manager/examples/src/main/resources/data/config.json -o /home/scaille/src/github/sky-lib/dataflow-manager/data-flow-manager/examples/src/main/java -f /home/scaille/src/github/sky-lib/dataflow-manager/data-flow-manager/examples/src/main/resources/data/broadcast-flow.json
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

// File generated from template
// SingleNodeWriter -c /home/scaille/src/github/sky-lib/dataflow-manager/data-flow-manager/examples/src/main/resources/data/config.json -o /home/scaille/src/github/sky-lib/dataflow-manager/data-flow-manager/examples/src/main/java -f /home/scaille/src/github/sky-lib/dataflow-manager/data-flow-manager/examples/src/main/resources/data/broadcast-flow.json
		int intValue;

		public int getIntValue() {
			return intValue;
		}

		public void setIntValue(int intValue) {
			this.intValue = intValue;
		}
	
	}