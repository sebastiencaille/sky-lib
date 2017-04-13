package ch.skymarshall.dataflowmgr.model;

public interface ExecutorFactory<ExecType> {

	<T extends FlowData> ExecType createNextDecisionPointExecution(final ActionPointReference<T> nextDp,
			final T nextDpInputData);

}
