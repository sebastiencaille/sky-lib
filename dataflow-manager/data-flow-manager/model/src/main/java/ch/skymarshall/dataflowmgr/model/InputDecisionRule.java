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

import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

/**
 * Transforms a data and send it to the next action
 *
 * @author scaille
 *
 * @param <InFlowType>
 * @param <InActionPointType>
 */
public class InputDecisionRule<InFlowType extends FlowData, InActionPointType extends FlowData> extends IDData {

	@FunctionalInterface
	public static interface CollectorFunction<InputDataType, DecisionPointDataType extends FlowData> {
		DecisionPointDataType apply(InputDataType inputData, ActionPoint<DecisionPointDataType, ?> ap, Registry reg);
	}

	private final Predicate<InFlowType> activationPredicate;
	private final ActionPoint<InActionPointType, ?> actionPointToExecute;
	private final CollectorFunction<InFlowType, InActionPointType> dataMuxFunction;
	private final Class<InFlowType> inputClass;

	public static <InFlow extends FlowData, ActPointInType extends FlowData> InputDecisionRule<InFlow, ActPointInType> input(
			final UUID uuid, final Class<InFlow> inputClass, final ActionPoint<ActPointInType, ?> actionPoint,
			final CollectorFunction<InFlow, ActPointInType> fillFunction, final Predicate<InFlow> activationPredicate) {
		return new InputDecisionRule<>(uuid, inputClass, activationPredicate, actionPoint, fillFunction);
	}

	/**
	 * Fills the input class with the incoming data using the fillFunction
	 *
	 * @param uuid
	 * @param inputClass
	 * @param activationPredicate
	 * @param actionPoint
	 * @param dataMuxFunction
	 * @return
	 */
	public static <InFlow extends FlowData, InActPointType extends FlowData> InputDecisionRule<InFlow, InActPointType> input(
			final UUID uuid, final Class<InFlow> inputClass, final Predicate<InFlow> activationPredicate,
			final ActionPoint<InActPointType, ?> actionPoint,
			final BiConsumer<InFlow, InActPointType> dataMuxFunction) {
		return new InputDecisionRule<>(uuid, inputClass, activationPredicate, actionPoint,
				new CollectorFunction<InFlow, InActPointType>() { // NOSONAR
					@Override
					public InActPointType apply(final InFlow incomingData, final ActionPoint<InActPointType, ?> ap,
							final Registry reg) {
						final InActPointType in = ap.get(reg, incomingData);
						dataMuxFunction.accept(incomingData, in);
						return in;
					}
				});
	}

	protected InputDecisionRule(final UUID uuid, final Class<InFlowType> inputClass,
			final Predicate<InFlowType> activationPredicate, final ActionPoint<InActionPointType, ?> ap,
			final CollectorFunction<InFlowType, InActionPointType> dataMuxFunction) {
		super(uuid);
		this.inputClass = inputClass;
		this.activationPredicate = activationPredicate;
		this.actionPointToExecute = ap;
		this.dataMuxFunction = dataMuxFunction;
	}

	public ActionPoint<?, ?> getActionPointToExecute() {
		return actionPointToExecute;
	}

	protected InActionPointType convertData(final FlowData inFlow, final Registry registry) {
		final InActionPointType apValue = dataMuxFunction.apply(inputClass.cast(inFlow), actionPointToExecute,
				registry);
		apValue.setContext(inFlow, actionPointToExecute.uuid());
		return apValue;
	}

	public boolean matches(final FlowData inputData) {
		return inputClass.isInstance(inputData) && activationPredicate.test(inputClass.cast(inputData));
	}

}
