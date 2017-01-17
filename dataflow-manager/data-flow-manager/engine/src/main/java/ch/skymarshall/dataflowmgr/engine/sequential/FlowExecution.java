package ch.skymarshall.dataflowmgr.engine.sequential;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import ch.skymarshall.dataflowmgr.engine.model.ExecutionReport;
import ch.skymarshall.dataflowmgr.engine.model.ExecutionReport.Event;
import ch.skymarshall.dataflowmgr.model.DecisionPoint;
import ch.skymarshall.dataflowmgr.model.DecisionRule;
import ch.skymarshall.dataflowmgr.model.ExecutorFactory;
import ch.skymarshall.dataflowmgr.model.Flow;
import ch.skymarshall.dataflowmgr.model.FlowData;
import ch.skymarshall.dataflowmgr.model.Registry;

public class FlowExecution<InputDataType extends FlowData> {

	/**
	 *
	 */
	private final Flow<InputDataType> flow;

	/**
	 * @param flow
	 */
	public FlowExecution(final Flow<InputDataType> flow) {
		this.flow = flow;
	}

	public void execute(final InputDataType inputData, final ExecutionReport report, final Registry registry) {
		final UUID flowExecution = UUID.randomUUID();
		report.add(Event.START_FLOW, this.flow.uuid(), flowExecution);

		final Set<DecisionPoint<?, ?>.ExecutionSteps> dpExecutions = new HashSet<>();

		inputData.setCurrentFlowExecution(flowExecution);
		dpExecutions
				.addAll(new DecisionPointExecutor(flow.getEntryPoint().execution(inputData), report).execute(registry));

		while (!dpExecutions.isEmpty()) {
			final Iterator<DecisionPoint<?, ?>.ExecutionSteps> iterator = dpExecutions.iterator();
			final DecisionPoint<?, ?>.ExecutionSteps next = iterator.next();
			iterator.remove();
			dpExecutions.addAll(new DecisionPointExecutor(next, report).execute(registry));
		}

	}

	public static class DecisionPointExecutor {

		private final DecisionPoint<?, ?>.ExecutionSteps execution;
		private final ExecutionReport report;

		public DecisionPointExecutor(final DecisionPoint<?, ?>.ExecutionSteps execution, final ExecutionReport report) {
			this.execution = execution;
			this.report = report;
		}

		public Set<DecisionPoint<?, ?>.ExecutionSteps> execute(final Registry registry) {
			if (!execution.isReady()) {
				report.add(Event.DP_NOT_READY, execution.uuid(), execution.currentFlowExecution());
				return Collections.emptySet();
			}
			report.add(Event.EXECUTE_DP, execution.uuid(), execution.currentFlowExecution());
			execution.executeAction();
			report.add(Event.SELECT_RULES, execution.uuid(), execution.currentFlowExecution());
			final List<DecisionRule<?, ?>> selectRules = execution.selectRules();
			if (execution.noRule()) {
				report.add(Event.ERROR, execution.uuid(), execution.currentFlowExecution());
				throw new IllegalStateException("No rule found: decision point=" + execution.uuid() + ", input="
						+ execution.inputToString() + ", output=" + execution.outputToString());
			}
			if (execution.stop()) {
				report.add(Event.STOP_RULE, execution.uuid(), execution.currentFlowExecution());
				return Collections.emptySet();
			}
			selectRules.stream()
					.forEach(r -> report.add(Event.SELECTED_RULE, r.uuid(), execution.currentFlowExecution()));
			return execution.getExecutions(registry, new ExecutorFactory<DecisionPoint<?, ?>.ExecutionSteps>() {

				@Override
				public <T extends FlowData> DecisionPoint<?, ?>.ExecutionSteps createNextDecisionPointExecution(
						final DecisionPoint<T, ?> nextDp, final T nextData) {
					return nextDp.execution(nextData);
				}
			});

		}

	}

}