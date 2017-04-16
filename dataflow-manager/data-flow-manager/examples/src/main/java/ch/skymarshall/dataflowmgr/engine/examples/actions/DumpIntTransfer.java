// File generated from template
package ch.skymarshall.dataflowmgr.engine.examples.actions;

import ch.skymarshall.dataflowmgr.model.FlowAction;
import ch.skymarshall.dataflowmgr.engine.examples.dto.IntTransfer;
import ch.skymarshall.dataflowmgr.model.NoData;


public class DumpIntTransfer extends FlowAction<IntTransfer, NoData> {

	public DumpIntTransfer() {
		super(IntTransfer.class, IntTransfer::new);
	}
	
	@Override
	public NoData apply(final IntTransfer input) {
		System.out.println(input);
			return NO_DATA;
	}

}