package ch.skymarshall.dataflowmgr.model;

public interface ExecutorFactory<ExecType> {

	<T extends FlowData> ExecType createNextDecisionPointExecution(final DecisionPoint<T, ?> nextDp,
			final T nextDpInputData);

}
