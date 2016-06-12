package ch.skymarshall.dataflowmgr.engine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import ch.skymarshall.dataflowmgr.engine.ExecutionReport.Event;

public class DecisionPoint<ContextType> {

	public class DecisionPointExecutor {

		private final ContextType context;
		private Optional<DecisionRule<ContextType>> executable;
		private final ExecutionReport report;

		public DecisionPointExecutor(final ContextType context, final ExecutionReport report) {
			this.context = context;
			this.report = report;
		}

		public boolean prepare() {
			final Stream<DecisionRule<ContextType>> executableRules = rules.stream().filter(in -> in.test(context));
			executable = executableRules.collect(StreamHelper.zeroOrOne())
					.orElseThrow(count -> new IllegalStateException(
							"Found too many rules for context: " + count + "; context=" + context));
			report.add(Event.PREPARE_DP, getName(), executable.isPresent());
			return executable.isPresent();
		}

		public FlowStatus execute() {
			report.add(Event.EXECUTE_DP, getName());
			final FlowStatus status = executable().execute(context, report);
			report.add(Event.DP_FINISHED, getName(), status);
			return status;
		}

		private DecisionRule<ContextType> executable() {
			return executable.orElseThrow(() -> new IllegalStateException("No rule to execute"));
		}

	}

	private final List<DecisionRule<ContextType>> rules = new ArrayList<>();
	private final String name;

	public DecisionPoint(final String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	@SuppressWarnings("unchecked")
	public DecisionPoint<ContextType> add(final DecisionRule<ContextType>... newRules) {
		rules.addAll(Arrays.asList(newRules));
		return this;
	}

	public void validate() {
		// Noop
	}

	public DecisionPointExecutor forContext(final ContextType context, final ExecutionReport report) {
		return new DecisionPointExecutor(context, report);
	}

	@Override
	public String toString() {
		return "Decision point";
	}

}
