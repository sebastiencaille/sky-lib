package ch.skymarshall.dataflowmgr.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import ch.skymarshall.dataflowmgr.engine.ExecutionReport.Event;

public class Flow<ContextType> {

	public class FlowExecution {

		public ContextType execute(final ContextType context, final ExecutionReport report) {

			report.add(Event.START_FLOW, getName());

			for (final List<DecisionPoint<ContextType>> step : flow) {
				final List<DecisionPoint<ContextType>.DecisionPointExecutor> foundExecutors = step.stream()
						.map((dp) -> dp.forContext(context, report)).filter(dpe -> dpe.prepare())
						.collect(Collectors.toList());

				final List<FlowStatus> results = foundExecutors.stream().map(executor -> executor.execute())
						.collect(Collectors.toList());
				final Optional<FlowStatus> foundResult = results.stream().filter(in -> in != FlowStatus.SPLIT)
						.collect(StreamHelper.zeroOrOne())
						.orElseThrow(count -> new IllegalStateException("Found too many results: " + count));
				if (foundResult.orElse(FlowStatus.CONTINUE) == FlowStatus.STOP) {
					break;
				}
			}
			return context;
		}

	}

	private final List<List<DecisionPoint<ContextType>>> flow = new ArrayList<>();
	private List<DecisionPoint<ContextType>> currentStep;
	private boolean built = false;
	private final String name;

	public Flow(final String name) {
		this.name = name;
		currentStep = new ArrayList<>();
	}

	public String getName() {
		return name;
	}

	public Flow<ContextType> add(final DecisionPoint<ContextType> point) {
		point.validate();
		currentStep.add(point);
		return this;
	}

	public Flow<ContextType> nextStep() {
		flow.add(currentStep);
		currentStep = new ArrayList<>();
		return this;
	}

	public FlowExecution build() {
		if (!built) {
			flow.add(currentStep);
		}
		built = true;
		return new FlowExecution();
	}

}
