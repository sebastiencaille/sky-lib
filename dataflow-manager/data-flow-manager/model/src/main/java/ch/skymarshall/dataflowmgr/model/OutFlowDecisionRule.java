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
package ch.skymarshall.dataflowmgr.model;

import java.util.UUID;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Convert from action point's output type to a flow type
 *
 * @author scaille
 *
 * @param <OutActionPointType>
 * @param <OutFlowType>
 */
public class OutFlowDecisionRule<OutActionPointType extends FlowData, OutFlowType extends FlowData> extends IDData {

	private final Predicate<OutActionPointType> activationPredicate;
	private final FlowActionType actionType;
	private final ActionPointReference<OutFlowType> nextActionPointRef;
	private final Function<OutActionPointType, OutFlowType> extractionFunction;

	/**
	 * Send rule to send output to next action point
	 *
	 * @param uuid
	 *            the rule's uuid
	 * @param activationPredicate
	 *            the predicate that enables this rule
	 * @param actionType
	 *            the flow action type
	 * @param actionPointReference
	 * @param apToFlow
	 *            the conversion of the action point's output to the data flow
	 * @return
	 */
	public static <OutActionPointType extends FlowData, OutFlowType extends FlowData> OutFlowDecisionRule<OutActionPointType, OutFlowType> output(
			final UUID uuid, final Predicate<OutActionPointType> activationPredicate, final FlowActionType actionType,
			final ActionPoint<OutFlowType, ?> ap, final Function<OutActionPointType, OutFlowType> apToFlow) {
		return new OutFlowDecisionRule<>(uuid, activationPredicate, actionType, LocalAPRef.local(ap), apToFlow);
	}

	/**
	 * Send rule to send output to next action point
	 *
	 * @param uuid
	 *            the rule's uuid
	 * @param activationPredicate
	 *            the predicate that enables this rule
	 * @param actionType
	 *            the flow action type
	 * @param actionPointReference
	 * @param apToFlow
	 *            the conversion of the action point's output to the data flow
	 * @return
	 */
	public static <OutActionPointType extends FlowData, OutFlowType extends FlowData> OutFlowDecisionRule<OutActionPointType, OutFlowType> output(
			final UUID uuid, final Predicate<OutActionPointType> activationPredicate, final FlowActionType actionType,
			final InFlowDecisionRule<OutFlowType, ?> ap, final Function<OutActionPointType, OutFlowType> apToFlow) {
		return new OutFlowDecisionRule<>(uuid, activationPredicate, actionType, LocalAPRef.local(ap), apToFlow);
	}

	/**
	 * Send rule to send output to next action point
	 *
	 * @param uuid
	 *            the rule's uuid
	 * @param activationPredicate
	 *            the predicate that enables this rule
	 * @param actionType
	 *            the flow action type
	 * @param actionPointReference
	 * @param apToFlow
	 *            the conversion of the action point's output to the data flow
	 * @return
	 */
	public static <OutActionPointType extends FlowData, OutFlowType extends FlowData> OutFlowDecisionRule<OutActionPointType, OutFlowType> output(
			final UUID uuid, final Predicate<OutActionPointType> activationPredicate, final FlowActionType actionType,
			final ActionPointReference<OutFlowType> actionPointReference,
			final Function<OutActionPointType, OutFlowType> apToFlow) {
		return new OutFlowDecisionRule<>(uuid, activationPredicate, actionType, actionPointReference, apToFlow);
	}

	protected OutFlowDecisionRule(final UUID uuid, final Predicate<OutActionPointType> activationPredicate,
			final FlowActionType actionType, final ActionPointReference<OutFlowType> nextActionPointRef,
			final Function<OutActionPointType, OutFlowType> extractionFunction) {
		super(uuid);
		this.activationPredicate = activationPredicate;
		this.actionType = actionType;
		this.nextActionPointRef = nextActionPointRef;
		this.extractionFunction = extractionFunction;
	}

	public Predicate<OutActionPointType> getActivationPredicate() {
		return activationPredicate;
	}

	public FlowActionType getActionType() {
		return actionType;
	}

	/**
	 * Creates an executor that will execute the next decision point, according
	 * to the output of this decision point
	 *
	 * @param actionPointData
	 * @param registry
	 * @return
	 */
	public <ExecutorType> ExecutorType createExecutor(final OutActionPointType actionPointData,
			final ExecutorFactory<ExecutorType> executorFactory) {
		final OutFlowType nextDPData = convertData(actionPointData);
		return executorFactory.createNextDecisionPointExecution(nextActionPointRef, nextDPData);
	}

	protected OutFlowType convertData(final OutActionPointType actionPointData) {
		final OutFlowType convertedValue = extractionFunction.apply(actionPointData);
		convertedValue.setContext(actionPointData, actionPointData.getSource());
		return convertedValue;
	}

}
