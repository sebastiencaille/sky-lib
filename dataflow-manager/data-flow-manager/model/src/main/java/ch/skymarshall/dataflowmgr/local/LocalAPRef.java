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
package ch.skymarshall.dataflowmgr.local;

import static java.util.stream.Collectors.toList;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import ch.skymarshall.dataflowmgr.model.ActionPoint;
import ch.skymarshall.dataflowmgr.model.ActionPointReference;
import ch.skymarshall.dataflowmgr.model.FlowData;
import ch.skymarshall.dataflowmgr.model.InputDecisionRule;

/**
 * Reference to an action point.
 *
 * @author scaille
 *
 * @param <I>
 *            the flow type injected into the reference (could be input flow or
 *            action point flow type)
 */
public class LocalAPRef<I extends FlowData> implements ActionPointReference<I> {

	private final Set<ActionPoint<?, ?>> actionPoints = new HashSet<>();

	public LocalAPRef(final ActionPoint<?, ?>... actionPoint) {
		addAll(actionPoint);
	}

	public Set<ActionPoint<?, ?>> getActionPoints() {
		return actionPoints;
	}

	public LocalAPRef<I> addAll(final ActionPoint<?, ?>... actionPoint) {
		actionPoints.addAll(Arrays.asList(actionPoint));
		return this;
	}

	@Override
	public LocalAPRef<I> addAll(final ActionPointReference<I> actionPointRefs) {
		actionPoints.addAll(((LocalAPRef<I>) actionPointRefs).getActionPoints());
		return this;
	}

	public static <T extends FlowData> ActionPointReference<T> refTo(final ActionPoint<T, ?>... ap) {
		return new LocalAPRef<>(ap);
	}

	public static <T extends FlowData> ActionPointReference<T> refToApOf(final InputDecisionRule<T, ?>... in) {
		return new LocalAPRef<>(Arrays.stream(in).map(InputDecisionRule::getActionPointToExecute)
				.collect(Collectors.toList()).toArray(new ActionPoint<?, ?>[0]));
	}

	public static <T extends FlowData> List<ActionPoint<?, ?>.ExecutionSteps> createExecution(
			final ActionPointReference<T> nextDp, final T nextData) {
		final Set<ActionPoint<?, ?>> actionPoints = LocalAPRef.class.cast(nextDp).getActionPoints();
		final List<ActionPoint<?, ?>.ExecutionSteps> collect = actionPoints.stream()
				.map(dp -> dp.createExecution(nextData)).collect(toList());
		return collect;
	}
}
