package ch.skymarshall.dataflowmgr.model;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * A decision point calls an action (eg a microservice) and takes a decision
 * according to the action's result
 *
 * @author scaille
 *
 * @param <InputDataType>
 * @param <OutputDataType>
 */
public class DecisionPoint<InputDataType extends FlowData, OutputDataType extends FlowData> extends IDData {

	/**
	 * Used to delay activation, eg to make a join on two flows
	 */
	private Predicate<InputDataType> activator = (t -> true);

	private final FlowAction<InputDataType, OutputDataType> action;

	private final List<DecisionRule<OutputDataType, ?>> decisionRules = new ArrayList<>();

	public DecisionPoint(final UUID uuid, final FlowAction<InputDataType, OutputDataType> action) {
		super(uuid);
		this.action = action;
	}

	public void setActivator(final Predicate<InputDataType> activator) {
		this.activator = activator;
	}

	public FlowAction<InputDataType, OutputDataType> getAction() {
		return action;
	}

	@SuppressWarnings("unchecked")
	public final DecisionPoint<InputDataType, OutputDataType> add(
			final List<DecisionRule<OutputDataType, ? extends FlowData>> newRules) {
		decisionRules.addAll(newRules);
		return this;
	}

	@SafeVarargs
	@SuppressWarnings("varargs")
	public final DecisionPoint<InputDataType, OutputDataType> add(
			final DecisionRule<OutputDataType, ? extends FlowData>... newRules) {
		decisionRules.addAll(Arrays.asList(newRules));
		return this;
	}

	public List<DecisionRule<OutputDataType, ?>> getRules() {
		return decisionRules;
	}

	public void validate() {
		// Noop
	}

	/**
	 * Steps of decision point execution
	 *
	 * @author scaille
	 *
	 */
	public class ExecutionSteps {

		private final InputDataType inputData;
		private OutputDataType outputData;
		private List<DecisionRule<OutputDataType, ?>> selectedRules;

		public ExecutionSteps(final InputDataType inputData) {
			this.inputData = inputData;
		}

		public boolean isReady() {
			return activator.test(inputData);
		}

		public void executeAction() {
			outputData = action.apply(inputData);
			outputData.setCurrentFlowExecution(inputData.getCurrentFlowExecution());
		}

		public List<DecisionRule<?, ?>> selectRules() {
			selectedRules = decisionRules.stream()
					.filter(rule -> rule.getActivationPredicates().stream().anyMatch(p -> p.test(outputData)))
					.collect(toList());
			return selectedRules.stream().map(r -> (DecisionRule<?, ?>) r).collect(Collectors.toList());
		}

		public <ExecType> Set<ExecType> getExecutions(final Registry registry,
				final ExecutorFactory<ExecType> executorFactory) {
			return selectedRules.stream().map(dr -> dr.executor(outputData, registry, executorFactory))
					.collect(Collectors.toSet());
		}

		public boolean noRule() {
			return selectedRules.isEmpty();
		}

		public String inputToString() {
			return "TODO";
		}

		public String outputToString() {
			return "TODO";
		}

		public boolean stop() {
			return selectedRules.stream().allMatch(r -> r.getActionType() == FlowActionType.STOP);
		}

		public UUID uuid() {
			return DecisionPoint.this.uuid();
		}

		public UUID currentFlowExecution() {
			return inputData.getCurrentFlowExecution();
		}

	}

	public DecisionPoint<InputDataType, OutputDataType>.ExecutionSteps execution(final InputDataType inputData) {
		return new ExecutionSteps(inputData);

	}

	@Override
	public String toString() {
		return "Decision point";
	}

	public static <InputDataType extends FlowData, OutputDataType extends FlowData> DecisionPoint<InputDataType, OutputDataType> simple(
			final UUID uuid, final FlowAction<InputDataType, OutputDataType> action) {
		return new DecisionPoint<>(uuid, action);
	}

	public static <InputDataType extends FlowData> DecisionPoint<InputDataType, ?> terminal(final UUID uuid,
			final FlowAction<InputDataType, NoData> action) {
		final DecisionPoint<InputDataType, NoData> decisionPoint = new DecisionPoint<>(uuid, action);
		decisionPoint.add(
				new DecisionRule<NoData, NoData>(NoData.NO_DATA.uuid(), FlowActionType.STOP, null, null, d -> true));
		return decisionPoint;
	}

}
