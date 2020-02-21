// File generated from template
// SingleNodeWriter -c /home/scaille/src/github/sky-lib/dataflow-manager/examples/src/main/resources/data/config.json -o /home/scaille/src/github/sky-lib/dataflow-manager/examples/src/main/java -f /home/scaille/src/github/sky-lib/dataflow-manager/examples/src/main/resources/data/simple-flow.json
package ch.skymarshall.dataflowmgr.engine.examples.actions;

import ch.skymarshall.dataflowmgr.model.FlowAction;
import ch.skymarshall.dataflowmgr.engine.examples.dto.IntTransfer;


public class IntTransferIdentity extends FlowAction<IntTransfer, IntTransfer> {

	public IntTransferIdentity() {
		super(IntTransfer.class, IntTransfer::new);
	}
	
	@Override
	public IntTransfer apply(final IntTransfer input) {
		return input;
	}

}