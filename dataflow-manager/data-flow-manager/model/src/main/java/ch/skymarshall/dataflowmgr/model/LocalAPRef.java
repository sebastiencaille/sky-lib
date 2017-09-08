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
package ch.skymarshall.dataflowmgr.model;

public class LocalAPRef<IDT extends FlowData> implements ActionPointReference<IDT> {

	private final ActionPoint<?, ?> actionPoint;

	private LocalAPRef(final ActionPoint<?, ?> actionPoint) {
		this.actionPoint = actionPoint;
	}

	public ActionPoint<?, ?> getActionPoint() {
		return actionPoint;
	}

	public static <T extends FlowData> ActionPointReference<T> local(final ActionPoint<T, ?> ap) {
		return new LocalAPRef<>(ap);
	}

	public static <T extends FlowData> ActionPointReference<T> local(final InputDecisionRule<T, ?> in) {
		return new LocalAPRef<>(in.getActionPointToExecute());
	}

	public static <T extends FlowData> ActionPoint<?, ?>.ExecutionSteps invoke(final ActionPointReference<T> nextDp,
			final T nextData) {
		return LocalAPRef.class.cast(nextDp).getActionPoint().invoke(nextData);
	}
}
