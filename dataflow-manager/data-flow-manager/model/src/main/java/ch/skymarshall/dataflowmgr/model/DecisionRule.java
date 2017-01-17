package ch.skymarshall.dataflowmgr.model;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class DecisionRule<OutputDataType extends FlowData, DecisionPointDataType extends FlowData> extends IDData {

	private final List<Predicate<OutputDataType>> activationPredicates;
	private final FlowActionType actionType;
	private final DecisionPoint<DecisionPointDataType, ?> decisionPoint;
	private final CollectorFunction<OutputDataType, DecisionPointDataType> dataConversionFunction;

	@SafeVarargs
	public DecisionRule(final UUID uuid, final FlowActionType actionType,
			final DecisionPoint<DecisionPointDataType, ?> dp,
			final CollectorFunction<OutputDataType, DecisionPointDataType> extractionFunction,
			final Predicate<OutputDataType>... activationPredicates) {
		super(uuid);
		this.activationPredicates = Arrays.asList(activationPredicates);
		this.actionType = actionType;
		this.decisionPoint = dp;
		this.dataConversionFunction = extractionFunction;
	}

	public List<Predicate<OutputDataType>> getActivationPredicates() {
		return activationPredicates;
	}

	public FlowActionType getActionType() {
		return actionType;
	}

	/**
	 * Creates an executor that will execute the next decision point, according
	 * to the output of this decision point
	 *
	 * @param dpOutputData
	 * @param registry
	 * @return
	 */
	public <ExecutorType> ExecutorType executor(final OutputDataType dpOutputData, final Registry registry,
			final ExecutorFactory<ExecutorType> executorFactory) {
		final DecisionPointDataType nextDPData = convertData(dpOutputData, registry);
		return executorFactory.createNextDecisionPointExecution(decisionPoint, nextDPData);
	}

	protected DecisionPointDataType convertData(final OutputDataType dpOutputData, final Registry registry) {
		final DecisionPointDataType convertedValue = dataConversionFunction.apply(dpOutputData, decisionPoint,
				registry);
		convertedValue.setCurrentFlowExecution(dpOutputData.getCurrentFlowExecution());
		return convertedValue;
	}

	@FunctionalInterface
	public static interface CollectorFunction<OutputDataType, DecisionPointDataType extends FlowData> {
		DecisionPointDataType apply(OutputDataType outputData, DecisionPoint<DecisionPointDataType, ?> dp,
				Registry reg);
	}

	public static <OutputDataType extends FlowData, DecisionPointDataType extends FlowData> CollectorFunction<OutputDataType, DecisionPointDataType> collector(
			final Supplier<DecisionPointDataType> newInstanceSupplier,
			final BiConsumer<OutputDataType, DecisionPointDataType> converter) {
		return new CollectorFunction<OutputDataType, DecisionPointDataType>() {
			@Override
			public DecisionPointDataType apply(final OutputDataType outputData,
					final DecisionPoint<DecisionPointDataType, ?> dp, final Registry reg) {
				final DecisionPointDataType dpInputData = reg.get(dp.uuid(), newInstanceSupplier);
				converter.accept(outputData, dpInputData);
				return dpInputData;
			}
		};
	}

}
