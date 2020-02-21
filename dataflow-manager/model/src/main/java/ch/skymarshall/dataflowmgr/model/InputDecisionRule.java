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
 * @param <IDT>
 * @param <ACTIDT>
 */
public class InputDecisionRule<IDT extends FlowData, ACTIDT extends FlowData> extends IDData {

	public static interface CollectorFunction<IDT extends FlowData, DecisionPointDataType extends FlowData> {
		DecisionPointDataType apply(IDT inputData, ActionPoint<DecisionPointDataType, ?> ap, Registry reg);
	}

	private final Predicate<IDT> activationPredicate;
	private final ActionPoint<ACTIDT, ?> actionPointToExecute;
	private final CollectorFunction<IDT, ACTIDT> dataMuxFunction;
	private final Class<IDT> inputClass;

	public static <IDT extends FlowData, ACTIDT extends FlowData> InputDecisionRule<IDT, ACTIDT> input(
			final UUID uuid, final Class<IDT> inputClass, final ActionPoint<ACTIDT, ?> actionPoint,
			final CollectorFunction<IDT, ACTIDT> fillFunction, final Predicate<IDT> activationPredicate) {
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
	public static <IDT extends FlowData, ACTIDT extends FlowData> InputDecisionRule<IDT, ACTIDT> input(
			final UUID uuid, final Class<IDT> inputClass, final Predicate<IDT> activationPredicate,
			final ActionPoint<ACTIDT, ?> actionPoint,
			final BiConsumer<IDT, ACTIDT> dataMuxFunction) {
		return new InputDecisionRule<>(uuid, inputClass, activationPredicate, actionPoint,
				new CollectorFunction<IDT, ACTIDT>() { // NOSONAR
					@Override
					public ACTIDT apply(final IDT incomingData, final ActionPoint<ACTIDT, ?> ap,
							final Registry reg) {
						final ACTIDT in = ap.get(reg, incomingData);
						dataMuxFunction.accept(incomingData, in);
						return in;
					}
				});
	}

	protected InputDecisionRule(final UUID uuid, final Class<IDT> inputClass, final Predicate<IDT> activationPredicate,
			final ActionPoint<ACTIDT, ?> ap,
			final CollectorFunction<IDT, ACTIDT> dataMuxFunction) {
		super(uuid);
		this.inputClass = inputClass;
		this.activationPredicate = activationPredicate;
		this.actionPointToExecute = ap;
		this.dataMuxFunction = dataMuxFunction;
	}

	public ActionPoint<?, ?> getActionPointToExecute() {
		return actionPointToExecute;
	}

	protected ACTIDT convertData(final FlowData inFlow, final Registry registry) {
		final ACTIDT apValue = dataMuxFunction.apply(inputClass.cast(inFlow), actionPointToExecute,
				registry);
		apValue.setContext(inFlow, actionPointToExecute.uuid());
		return apValue;
	}

	public boolean matches(final FlowData inputData) {
		return inputClass.isInstance(inputData) && activationPredicate.test(inputClass.cast(inputData));
	}

}
