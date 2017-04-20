package ch.skymarshall.dataflowmgr.model;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import ch.skymarshall.dataflowmgr.model.InFlowDecisionRule.CollectorFunction;

/**
 * A decision point calls an action (eg a microservice) and takes a decision
 * according to the action's result
 *
 * @author scaille
 *
 * @param <InputDataType>
 * @param <OutputDataType>
 */
public class ActionPoint<InputDataType extends FlowData, OutputDataType extends FlowData> extends IDData {

	/**
	 * Used to delay activation, eg to make a join on two flows
	 */
	private Predicate<InputDataType> activator = (t -> true);

	private final FlowAction<InputDataType, OutputDataType> action;

	private final List<InFlowDecisionRule<?, InputDataType>> inputDecisionRules = new ArrayList<>();
	private final List<OutFlowDecisionRule<OutputDataType, ?>> outputDecisionRules = new ArrayList<>();

	public ActionPoint(final UUID uuid, final FlowAction<InputDataType, OutputDataType> action) {
		super(uuid);
		this.action = action;
	}

	public void setActivator(final Predicate<InputDataType> activator) {
		this.activator = activator;
	}

	public FlowAction<InputDataType, OutputDataType> getAction() {
		return action;
	}

	public final <T extends FlowData> InFlowDecisionRule<T, InputDataType> addInFlowRule(final UUID uuid,
			final Class<T> inputClass, final Predicate<T> activationPredicate,
			final BiConsumer<T, InputDataType> collectFunction) {
		final InFlowDecisionRule<T, InputDataType> rule = new InFlowDecisionRule<>(uuid, inputClass,
				activationPredicate, this, new CollectorFunction<T, InputDataType>() {
					@Override
					public InputDataType apply(final T inputData, final ActionPoint<InputDataType, ?> ap,
							final Registry reg) {
						final InputDataType ApInputData = ap.get(reg, inputData);
						collectFunction.accept(inputData, ApInputData);
						return ApInputData;
					}
				});
		inputDecisionRules.add(rule);
		return rule;
	}

	public final <T extends FlowData> InFlowDecisionRule<T, InputDataType> addInFlowRule(final UUID uuid,
			final Class<T> inputClass, final Predicate<T> activationPredicate,
			final Function<T, InputDataType> collectFunction) {
		final InFlowDecisionRule<T, InputDataType> rule = new InFlowDecisionRule<>(uuid, inputClass,
				activationPredicate, this, new CollectorFunction<T, InputDataType>() {
					@Override
					public InputDataType apply(final T inputData, final ActionPoint<InputDataType, ?> ap,
							final Registry reg) {
						return collectFunction.apply(inputData);
					}
				});
		inputDecisionRules.add(rule);
		return rule;
	}

	public void addOutputRule(final List<OutFlowDecisionRule<OutputDataType, ? extends FlowData>> newRules) {
		outputDecisionRules.addAll(newRules);
	}

	@SafeVarargs
	public final void addOutputRule(final OutFlowDecisionRule<OutputDataType, ? extends FlowData>... newRules) {
		outputDecisionRules.addAll(Arrays.asList(newRules));
	}

	public List<OutFlowDecisionRule<OutputDataType, ?>> getOutputRules() {
		return outputDecisionRules;
	}

	public void validate() {
		// Noop
	}

	/**
	 * Steps of the execution of the decision point
	 *
	 * @author scaille
	 *
	 */
	public class ExecutionSteps {

		private final FlowData untypedInputData;
		private InputDataType inputData;
		private OutputDataType outputData;
		private List<OutFlowDecisionRule<OutputDataType, ?>> selectedRules;

		private ExecutionSteps(final FlowData untypedInputData) {
			this.untypedInputData = untypedInputData;
		}

		/**
		 * Creates the input data using the input rules
		 *
		 * @param registry
		 * @param orElse
		 *            consumer executed in case of error
		 */
		public UUID executeInputRule(final Registry registry, final Consumer<String> orElse) {
			final List<InFlowDecisionRule<?, InputDataType>> inputSelectedRules = inputDecisionRules.stream()
					.filter(rule -> rule.matches(untypedInputData)).collect(toList());
			if (inputSelectedRules.size() == 1) {
				inputData = inputSelectedRules.get(0).convertData(untypedInputData, registry);
				return inputSelectedRules.get(0).uuid();

			} else if (inputSelectedRules.isEmpty()) {
				// implicit identity conversion
				if (!action.getInputClass().isInstance(untypedInputData)) {
					orElse.accept("BadDefaultInputRule: " + action.getInputClass().getName() + ","
							+ untypedInputData.getClass().getName());
				}
				inputData = action.getInputClass().cast(untypedInputData);
			} else {
				orElse.accept("TooManyInputRules");
			}
			return null;
		}

		/**
		 * Executes the action according to the input data extracted by
		 * executeInputRules
		 *
		 * @return
		 */
		public boolean executeAction() {
			if (!activator.test(inputData)) {
				return false;
			}

			outputData = action.apply(inputData);
			if (outputData == null) {
				throw new IllegalStateException("Action point returned Null data: " + uuid());
			}
			outputData.setContext(inputData, this.uuid());
			return true;
		}

		public List<OutFlowDecisionRule<?, ?>> selectOutputRules(final Consumer<String> orElse) {
			selectedRules = outputDecisionRules.stream().filter(rule -> rule.getActivationPredicate().test(outputData))
					.collect(toList());
			if (selectedRules.isEmpty()) {
				orElse.accept("NoOutputRule");
			}
			return selectedRules.stream().map(r -> (OutFlowDecisionRule<?, ?>) r).collect(Collectors.toList());
		}

		/**
		 * Creates the executors that will execute the next action points
		 *
		 * @param registry
		 * @param executorFactory
		 * @return the next action points
		 */
		public <ExecType> Set<ExecType> createExecutions(final Registry registry,
				final ExecutorFactory<ExecType> executorFactory) {
			return selectedRules.stream().map(dr -> dr.createExecutor(outputData, executorFactory))
					.collect(Collectors.toSet());
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
			return ActionPoint.this.uuid();
		}

		public UUID currentFlowExecution() {
			return untypedInputData.getCurrentFlowExecution();
		}

	}

	/**
	 * Inject an arbitrary data into the action point
	 *
	 * @param inputData
	 * @return
	 */
	public ActionPoint<InputDataType, OutputDataType>.ExecutionSteps invoke(final FlowData inputData) {
		return new ExecutionSteps(inputData);
	}

	public static <InputDataType extends FlowData, OutputDataType extends FlowData, APRef extends ActionPointReference<?>> ActionPoint<InputDataType, OutputDataType> simple(
			final UUID uuid, final FlowAction<InputDataType, OutputDataType> action) {
		return new ActionPoint<>(uuid, action);
	}

	public static <InputDataType extends FlowData, APRef extends ActionPointReference<NoData>> ActionPoint<InputDataType, ?> terminal(
			final UUID uuid, final FlowAction<InputDataType, NoData> action) {
		final ActionPoint<InputDataType, NoData> decisionPoint = new ActionPoint<>(uuid, action);
		decisionPoint.addOutputRule(new OutFlowDecisionRule<NoData, NoData>(NoData.NO_DATA.uuid(), d -> true,
				FlowActionType.STOP, null, null));
		return decisionPoint;
	}

	public InputDataType get(final Registry registry, final FlowData data) {
		return registry.get(data.getCurrentFlowExecution(), action.getInputDataSupplier());
	}

}
