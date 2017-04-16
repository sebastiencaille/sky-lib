// File generated from template
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