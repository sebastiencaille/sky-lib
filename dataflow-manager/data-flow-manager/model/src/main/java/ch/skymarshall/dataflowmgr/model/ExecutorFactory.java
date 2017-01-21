package ch.skymarshall.dataflowmgr.model;

public interface ExecutorFactory<ExecType> {

	<T extends FlowData> ExecType createNextDecisionPointExecution(final ActionPoint<?, ?> nextDp,
			final T nextDpInputData);

}
