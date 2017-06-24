/*******************************************************************************
 * Copyright (c) 2017 Sebastien Caille.
 *  All rights reserved.
 * 
 *  Redistribution and use in source and binary forms are permitted
 *  provided that the above Copyrightnotice and this paragraph are
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

import java.util.function.Supplier;

import ch.skymarshall.dataflowmgr.model.ActionPoint;
import ch.skymarshall.dataflowmgr.model.Flow;
import ch.skymarshall.dataflowmgr.model.FlowAction;
import ch.skymarshall.dataflowmgr.model.FlowActionType;
import ch.skymarshall.dataflowmgr.model.FlowData;
import ch.skymarshall.dataflowmgr.model.NoData;
import ch.skymarshall.dataflowmgr.model.OutFlowDecisionRule;
import ch.skymarshall.dataflowmgr.model.Registry;

public class SimpleFlowFactory {

	public static class IntTransfer extends FlowData {
		int value;

		public IntTransfer(final int value) {
			super(newUuid(IntTransfer.class));
			this.value = value;
		}

		public IntTransfer() {
			super(newUuid(IntTransfer.class));
		}

	}

	public static class IdentityAction<T extends FlowData> extends FlowAction<T, T> {

		public IdentityAction(final Class<T> clazz, final Supplier<T> newInstance) {
			super(clazz, newInstance);
		}

		@Override
		public T apply(final T t) {
			return t;
		}

	}

	public static class IntTransferIdentity extends IdentityAction<IntTransfer> {

		public IntTransferIdentity() {
			super(IntTransfer.class, IntTransfer::new);
		}

	}

	public static class DumpIntTransfer extends FlowAction<IntTransfer, NoData> {
		public DumpIntTransfer() {
			super(IntTransfer.class, IntTransfer::new);
		}

		@Override
		public NoData apply(final IntTransfer t) {
			System.out.println(t.value);
			return NO_DATA;
		}

	}

	public static Flow<IntTransfer> simpleFlow(final Registry registry) {
		final ActionPoint<IntTransfer, IntTransfer> action1 = ActionPoint.simple(uuid(), new IntTransferIdentity());
		final ActionPoint<IntTransfer, ?> action2 = ActionPoint.terminal(uuid(), new DumpIntTransfer());

		final OutFlowDecisionRule<IntTransfer, IntTransfer> a1Toa2 = OutFlowDecisionRule.output(uuid(), (t) -> true,
				FlowActionType.CONTINUE, action2, (d) -> d);
		action1.addOutputRule(a1Toa2);

		registry.registerObject(action1, "action1");
		registry.registerObject(action1, "action1");
		registry.registerObject(a1Toa2, "a1Toa2");

		return new Flow<>(uuid(), action1);
	}

}
