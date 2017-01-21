package ch.skymarshall.dataflowmgr.engine.sequential;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

import ch.skymarshall.dataflowmgr.engine.model.ExecutionReport;
import ch.skymarshall.dataflowmgr.engine.model.ExecutionReport.Event;
import ch.skymarshall.dataflowmgr.model.ActionPoint;
import ch.skymarshall.dataflowmgr.model.ExecutorFactory;
import ch.skymarshall.dataflowmgr.model.Flow;
import ch.skymarshall.dataflowmgr.model.FlowData;
import ch.skymarshall.dataflowmgr.model.OutFlowDecisionRule;
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

		final Set<ActionPoint<?, ?>.ExecutionSteps> dpExecutions = new HashSet<>();

		inputData.setCurrentFlowExecution(flowExecution);
		dpExecutions.addAll(new ActionPointExecutor(flow.getEntryPoint().inject(inputData), report).execute(registry));

		while (!dpExecutions.isEmpty()) {
			final Iterator<ActionPoint<?, ?>.ExecutionSteps> iterator = dpExecutions.iterator();
			final ActionPoint<?, ?>.ExecutionSteps next = iterator.next();
			iterator.remove();
			dpExecutions.addAll(new ActionPointExecutor(next, report).execute(registry));
		}

	}

	public static class ActionPointExecutor {

		private final ActionPoint<?, ?>.ExecutionSteps execution;
		private final ExecutionReport report;

		public ActionPointExecutor(final ActionPoint<?, ?>.ExecutionSteps execution, final ExecutionReport report) {
			this.execution = execution;
			this.report = report;
		}

		public Set<ActionPoint<?, ?>.ExecutionSteps> execute(final Registry registry) {

			final Consumer<String> handleError = e -> {
				report.add(Event.ERROR, execution.uuid(), execution.currentFlowExecution(), e);
				throw new IllegalStateException(e + ": decision point=" + execution.uuid() + ", input="
						+ execution.inputToString() + ", output=" + execution.outputToString());
			};

			report.add(Event.HANDLE_INPUT, execution.uuid(), execution.currentFlowExecution());
			execution.executeInputRules(registry, handleError);

			report.add(Event.EXECUTE_AP, execution.uuid(), execution.currentFlowExecution());
			if (!execution.executeAction()) {
				report.add(Event.AP_NOT_READY, execution.uuid(), execution.currentFlowExecution());
				return Collections.emptySet();
			}

			report.add(Event.SELECT_OUTPUT_RULES, execution.uuid(), execution.currentFlowExecution());
			final List<OutFlowDecisionRule<?, ?>> selectRules = execution.selectOutputRules(handleError);
			if (execution.stop()) {
				report.add(Event.STOP_RULE, execution.uuid(), execution.currentFlowExecution());
				return Collections.emptySet();
			}
			selectRules.stream()
					.forEach(r -> report.add(Event.SELECTED_OUTPUT_RULE, r.uuid(), execution.currentFlowExecution()));
			return execution.createExecutions(registry, new ExecutorFactory<ActionPoint<?, ?>.ExecutionSteps>() {

				@Override
				public <T extends FlowData> ActionPoint<?, ?>.ExecutionSteps createNextDecisionPointExecution(
						final ActionPoint<?, ?> nextDp, final T nextData) {
					return nextDp.inject(nextData);
				}
			});

		}

	}

}