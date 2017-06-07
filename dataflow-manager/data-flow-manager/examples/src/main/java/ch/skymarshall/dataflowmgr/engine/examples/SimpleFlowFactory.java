/*******************************************************************************
 * Copyright (c) 2017 Sebastien Caille.
 *  All rights reserved.
 * 
 *  Redistribution and use in source and binary forms are permitted
 *  provided that the above copyright notice and this paragraph are
 *  duplicated in all such forms and that any documentation,
 *  advertising materials, and other materials related to such
 *  distribution and use acknowledge that the software was developed
 *  by Sebastien Caille.  The name of Sebastien Caille may not be used to endorse or promote products derived
 *  from this software without specific prior written permission.
 *  THIS SOFTWARE IS PROVIDED ``AS IS'' AND WITHOUT ANY EXPRESS OR
 *  IMPLIED WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 ******************************************************************************/
// File generated from template
package ch.skymarshall.dataflowmgr.engine.examples;

import ch.skymarshall.dataflowmgr.model.ActionPoint;
import ch.skymarshall.dataflowmgr.model.Flow;
import ch.skymarshall.dataflowmgr.model.FlowActionType;
import ch.skymarshall.dataflowmgr.model.InFlowDecisionRule;
import ch.skymarshall.dataflowmgr.model.OutFlowDecisionRule;
import ch.skymarshall.dataflowmgr.model.Registry;

import java.util.UUID;
import ch.skymarshall.dataflowmgr.engine.examples.actions.IntTransferIdentity;
import ch.skymarshall.dataflowmgr.engine.examples.actions.DumpIntTransfer;
import ch.skymarshall.dataflowmgr.model.LocalAPRef;
import ch.skymarshall.dataflowmgr.engine.examples.dto.IntTransfer;
import ch.skymarshall.dataflowmgr.model.NoData;


public class SimpleFlowFactory {

	public static Flow<IntTransfer> create(final Registry registry) {
		ActionPoint<IntTransfer, IntTransfer> intTransferIdentity = ActionPoint.simple(UUID.fromString("252663c7-305a-4867-ba24-855656d1e2f9"), new IntTransferIdentity());
ActionPoint<IntTransfer, ?> dumpIntTransfer = ActionPoint.terminal(UUID.fromString("6c072b83-e71f-4307-8a0d-86724e014bd6"), new DumpIntTransfer());

		final InFlowDecisionRule<IntTransfer,IntTransfer> intTransferIdentity_in_45d3a926_6e9c_48d1_95d9_6ffc071b8f3d = intTransferIdentity.addInFlowRule(UUID.fromString("45d3a926-6e9c-48d1-95d9-6ffc071b8f3d"), IntTransfer.class, (flowIn) -> true, (flowIn) -> flowIn);

		final OutFlowDecisionRule<IntTransfer, IntTransfer> intTransferIdentity_out_8a3152fb_4d16_4ff7_92a4_bce11cc30987 = OutFlowDecisionRule.output(UUID.fromString("8a3152fb-4d16-4ff7-92a4-bce11cc30987"), (apOut) -> true, FlowActionType.CONTINUE, LocalAPRef.local(dumpIntTransfer), (apOut) ->  apOut);
intTransferIdentity.addOutputRule(intTransferIdentity_out_8a3152fb_4d16_4ff7_92a4_bce11cc30987);

		registry.registerObject(intTransferIdentity_out_8a3152fb_4d16_4ff7_92a4_bce11cc30987, "intTransferIdentity_out_8a3152fb_4d16_4ff7_92a4_bce11cc30987");
registry.registerObject(intTransferIdentity_in_45d3a926_6e9c_48d1_95d9_6ffc071b8f3d, "intTransferIdentity_in_45d3a926_6e9c_48d1_95d9_6ffc071b8f3d");
registry.registerObject(dumpIntTransfer, "DumpIntTransfer");
registry.registerObject(intTransferIdentity, "IntTransferIdentity");

	
		return new Flow<>(UUID.fromString("f35b780b-bb4d-4010-83eb-6222ac947a0f"), intTransferIdentity);
	}
	

}
