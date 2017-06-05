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
import ch.skymarshall.dataflowmgr.model.ActionPointReference;
import ch.skymarshall.dataflowmgr.model.ExecutorFactory;
import ch.skymarshall.dataflowmgr.model.Flow;
import ch.skymarshall.dataflowmgr.model.FlowData;
import ch.skymarshall.dataflowmgr.model.LocalAPRef;
import ch.skymarshall.dataflowmgr.model.OutFlowDecisionRule;
import ch.skymarshall.dataflowmgr.model.Registry;

/**
 * Sequential flow execution
 *
 * @author scaille
 *
 * @param <InputDataType>
 */
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
		dpExecutions.addAll(new ActionPointExecutor(flow.getEntryPoint().invoke(inputData), report).execute(registry));

		while (!dpExecutions.isEmpty()) {
			final Iterator<ActionPoint<?, ?>.ExecutionSteps> iterator = dpExecutions.iterator();
			final ActionPoint<?, ?>.ExecutionSteps next = iterator.next();
			iterator.remove();
			dpExecutions.addAll(new ActionPointExecutor(next, report).execute(registry));
		}

	}

	/**
	 * Executes the action point logic and accordingly generates a report
	 *
	 * @author scaille
	 *
	 */
	public static class ActionPointExecutor {

		private final ActionPoint<?, ?>.ExecutionSteps apExecution;
		private final ExecutionReport report;

		public ActionPointExecutor(final ActionPoint<?, ?>.ExecutionSteps execution, final ExecutionReport report) {
			this.apExecution = execution;
			this.report = report;
		}

		public Set<ActionPoint<?, ?>.ExecutionSteps> execute(final Registry registry) {

			final Consumer<String> handleError = e -> {
				report.add(Event.ERROR, apExecution.uuid(), apExecution.currentFlowExecution(), e);
				throw new IllegalStateException(e + ": decision point=" + apExecution.uuid() + ", input="
						+ apExecution.inputToString() + ", output=" + apExecution.outputToString());
			};

			report.add(Event.SELECT_AND_EXEC_INPUT_RULE, apExecution.uuid(), apExecution.currentFlowExecution());
			final UUID executedInputRule = apExecution.executeInputRule(registry, handleError);
			if (executedInputRule != null) {
				report.add(Event.EXECUTED_INPUT_RULE, executedInputRule, apExecution.currentFlowExecution());
			}

			report.add(Event.EXECUTE_AP, apExecution.uuid(), apExecution.currentFlowExecution());
			if (!apExecution.executeAction()) {
				report.add(Event.AP_NOT_READY, apExecution.uuid(), apExecution.currentFlowExecution());
				return Collections.emptySet();
			}

			report.add(Event.SELECT_OUTPUT_RULES, apExecution.uuid(), apExecution.currentFlowExecution());
			final List<OutFlowDecisionRule<?, ?>> selectRules = apExecution.selectOutputRules(handleError);
			if (apExecution.stop()) {
				report.add(Event.STOP_RULE, apExecution.uuid(), apExecution.currentFlowExecution());
				return Collections.emptySet();
			}
			selectRules.stream()
					.forEach(r -> report.add(Event.SELECTED_OUTPUT_RULE, r.uuid(), apExecution.currentFlowExecution()));
			return apExecution.createExecutions(registry, new ExecutorFactory<ActionPoint<?, ?>.ExecutionSteps>() {

				@Override
				public <T extends FlowData> ActionPoint<?, ?>.ExecutionSteps createNextDecisionPointExecution(
						final ActionPointReference<T> nextDp, final T nextData) {
					return LocalAPRef.invoke(nextDp, nextData);

				}
			});

		}

	}

}