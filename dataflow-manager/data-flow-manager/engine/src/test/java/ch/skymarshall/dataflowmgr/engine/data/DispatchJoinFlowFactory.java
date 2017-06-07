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
package ch.skymarshall.dataflowmgr.engine.data;

import static ch.skymarshall.dataflowmgr.engine.data.UUIDFactory.newUuid;
import static ch.skymarshall.dataflowmgr.engine.data.UUIDFactory.uuid;

import ch.skymarshall.dataflowmgr.engine.data.SimpleFlowFactory.IntTransfer;
import ch.skymarshall.dataflowmgr.engine.data.SimpleFlowFactory.IntTransferIdentity;
import ch.skymarshall.dataflowmgr.model.ActionPoint;
import ch.skymarshall.dataflowmgr.model.Flow;
import ch.skymarshall.dataflowmgr.model.FlowAction;
import ch.skymarshall.dataflowmgr.model.FlowActionType;
import ch.skymarshall.dataflowmgr.model.FlowData;
import ch.skymarshall.dataflowmgr.model.InFlowDecisionRule;
import ch.skymarshall.dataflowmgr.model.NoData;
import ch.skymarshall.dataflowmgr.model.OutFlowDecisionRule;
import ch.skymarshall.dataflowmgr.model.Registry;

public class DispatchJoinFlowFactory {

	public static class Data2a extends FlowData {
		int value;

		public Data2a() {
			this(0);
		}

		public Data2a(final int value) {
			super(newUuid(Data2a.class));
			this.value = value;
		}

	}

	public static class Data2b extends FlowData {
		int value;
		int value2b;

		public Data2b() {
			this(0);
		}

		public Data2b(final int value) {
			super(newUuid(Data2b.class));
			this.value = value;
		}

	}

	public static class Data2ab extends FlowData {

		private IntTransfer data2a;
		private IntTransfer data2b;

		public Data2ab() {
			super(newUuid(Data2ab.class));
		}

		public IntTransfer getData2a() {
			return data2a;
		}

		public void setData2a(final IntTransfer data1b) {
			this.data2a = data1b;
		}

		public IntTransfer getData2b() {
			return data2b;
		}

		public void setData2b(final IntTransfer data2) {
			this.data2b = data2;
		}
	}

	public class IdentityActionData2b extends FlowAction<Data2b, Data2b> {

		public IdentityActionData2b() {
			super(Data2b.class, Data2b::new);
		}

		@Override
		public Data2b apply(final Data2b t) {
			return t;
		}

	}

	public static class SetOutputData2aAnd2b extends FlowAction<IntTransfer, Data2ab> {
		public SetOutputData2aAnd2b() {
			super(IntTransfer.class, IntTransfer::new);
		}

		@Override
		public Data2ab apply(final IntTransfer t) {
			final Data2ab output = new Data2ab();
			output.data2a = new IntTransfer(t.value);
			output.data2b = new IntTransfer(t.value + 1);
			return output;
		}
	}

	public static class DumpInputDp2ab extends FlowAction<Data2ab, NoData> {
		public DumpInputDp2ab() {
			super(Data2ab.class, Data2ab::new);
		}

		@Override
		public NoData apply(final Data2ab t) {
			System.out.println(t.data2a.value + " " + t.data2b.value);
			return NO_DATA;
		}

	}

	public static Flow<IntTransfer> dispatchJoinFlow(final Registry registry) {
		final ActionPoint<IntTransfer, Data2ab> ap1 = ActionPoint.simple(uuid(), new SetOutputData2aAnd2b());
		final ActionPoint<IntTransfer, IntTransfer> ap2a = ActionPoint.simple(uuid(), new IntTransferIdentity());
		final ActionPoint<IntTransfer, IntTransfer> ap2b = ActionPoint.simple(uuid(), new IntTransferIdentity());
		final ActionPoint<Data2ab, ?> joinPoint = ActionPoint.terminal(uuid(), new DumpInputDp2ab());
		joinPoint.setActivator((in) -> in.data2a != null && in.data2b != null);
		registry.registerObject(ap1, "ap1");
		registry.registerObject(ap2a, "ap2a");
		registry.registerObject(ap2b, "ap2b");
		registry.registerObject(joinPoint, "joinPoint");

		// All in
		final InFlowDecisionRule<IntTransfer, Data2ab> joinIn2a = joinPoint.addInFlowRule(uuid(), IntTransfer.class,
				(in) -> in.getSource().equals(ap2a.uuid()), (in, d) -> d.data2a = in);
		final InFlowDecisionRule<IntTransfer, Data2ab> joinIn2b = joinPoint.addInFlowRule(uuid(), IntTransfer.class,
				(in) -> in.getSource().equals(ap2b.uuid()), (in, d) -> d.data2b = in);
		registry.registerObject(joinIn2a, "joinIn2a");
		registry.registerObject(joinIn2b, "joinIn2b");

		// All out
		final OutFlowDecisionRule<Data2ab, IntTransfer> ap1ToAp2a = OutFlowDecisionRule.output(uuid(),
				d -> d.getData2a() != null, FlowActionType.CONTINUE, ap2a, (d) -> d.data2a);
		final OutFlowDecisionRule<Data2ab, IntTransfer> ap1ToAp2b = OutFlowDecisionRule.output(uuid(),
				d -> d.getData2b() != null, FlowActionType.CONTINUE, ap2b, (d) -> d.data2b);
		ap1.addOutputRule(ap1ToAp2a, ap1ToAp2b);

		final OutFlowDecisionRule<IntTransfer, IntTransfer> ap2aToJoin = OutFlowDecisionRule.output(uuid(),
				(out) -> true, FlowActionType.CONTINUE, joinIn2a, (out) -> out);
		final OutFlowDecisionRule<IntTransfer, IntTransfer> ap2bToJoin = OutFlowDecisionRule.output(uuid(),
				(out) -> true, FlowActionType.CONTINUE, joinIn2b, (out) -> out);
		ap2a.addOutputRule(ap2aToJoin);
		ap2b.addOutputRule(ap2bToJoin);

		registry.registerObject(ap1ToAp2a, "ap1ToAp2a");
		registry.registerObject(ap1ToAp2b, "ap1ToAp2b");

		registry.registerObject(ap2aToJoin, "ap2aToJoin");
		registry.registerObject(ap2bToJoin, "ap2bToJoin");

		return new Flow<>(uuid(), ap1);
	}

}
