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
	private final ActionPoint<?, ?> nextActionPoint;
	private final Function<OutActionPointType, OutFlowType> extractionFunction;

	public static <OutActionPointType extends FlowData, OutFlowType extends FlowData> OutFlowDecisionRule<OutActionPointType, OutFlowType> output(
			final UUID uuid, final FlowActionType actionType, final ActionPoint<OutFlowType, ?> actionPoint,
			final Function<OutActionPointType, OutFlowType> apToFlow,
			final Predicate<OutActionPointType> activationPredicate) {
		return new OutFlowDecisionRule<>(uuid, actionType, actionPoint, apToFlow, activationPredicate);
	}

	public static <OutActionPointType extends FlowData, OutFlowType extends FlowData> OutFlowDecisionRule<OutActionPointType, OutFlowType> output(
			final UUID uuid, final FlowActionType actionType, final InFlowDecisionRule<OutFlowType, ?> inputRule,
			final Function<OutActionPointType, OutFlowType> apToFlow,
			final Predicate<OutActionPointType> activationPredicate) {
		return new OutFlowDecisionRule<>(uuid, actionType, inputRule.getActionPointToExecute(), apToFlow,
				activationPredicate);
	}

	protected OutFlowDecisionRule(final UUID uuid, final FlowActionType actionType,
			final ActionPoint<?, ?> nextActionPoint, final Function<OutActionPointType, OutFlowType> extractionFunction,
			final Predicate<OutActionPointType> activationPredicate) {
		super(uuid);
		this.activationPredicate = activationPredicate;
		this.actionType = actionType;
		this.nextActionPoint = nextActionPoint;
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
	public <ExecutorType> ExecutorType executor(final OutActionPointType actionPointData,
			final ExecutorFactory<ExecutorType> executorFactory) {
		final OutFlowType nextDPData = convertData(actionPointData);
		return executorFactory.createNextDecisionPointExecution(nextActionPoint, nextDPData);
	}

	protected OutFlowType convertData(final OutActionPointType actionPointData) {
		final OutFlowType convertedValue = extractionFunction.apply(actionPointData);
		convertedValue.setContext(actionPointData, actionPointData.getSource());
		return convertedValue;
	}

}
