/*******************************************************************************
 * Copyright (c) 2017 Sebastien Caille.
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms are permitted
 *  provided that the above Copyrightnotice and this paragraph are
 *  duplicated in all such forms and that any documentation,
 *  advertising materials, and other materials related to such
 *  distribution and use acknowledge that the software was developed
 *  by Sebastien Caille.  The name of Sebastien Caille may not be used to endorse or promote products derived
 *  from this software without specific prior written permission.
 *  THIS SOFTWARE IS PROVIDED ``AS IS'' AND WITHOUT ANY EXPRESS OR
 *  IMPLIED WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 ******************************************************************************/
package ch.skymarshall.dataflowmgr.model;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import ch.skymarshall.dataflowmgr.model.InputDecisionRule.CollectorFunction;

/**
 * An action point calls an action (eg a microservice) and takes a decision
 * according to the action's output
 *
 * @author scaille
 *
 * @param <IDT>
 * @param <ODT>
 */
public class ActionPoint<IDT extends FlowData, ODT extends FlowData> extends IDData {

	/**
	 * Used to delay activation, eg to make a join on two flows
	 */
	private Predicate<IDT> activator = (t -> true);

	private final FlowAction<IDT, ODT> action;

	private final List<InputDecisionRule<?, IDT>> inputDecisionRules = new ArrayList<>();
	private final List<OutputDecisionRule<ODT, ?>> outputDecisionRules = new ArrayList<>();

	public ActionPoint(final UUID uuid, final FlowAction<IDT, ODT> action) {
		super(uuid);
		this.action = action;
	}

	public void setActivator(final Predicate<IDT> activator) {
		this.activator = activator;
	}

	public FlowAction<IDT, ODT> getAction() {
		return action;
	}

	/**
	 * Adds an input rule
	 *
	 * @param uuid
	 * @param inputClass
	 * @param activationPredicate
	 * @param collectFunction
	 *            function that merges the flow's input into the action point's
	 *            input
	 * @return
	 */
	public final <T extends FlowData> InputDecisionRule<T, IDT> addInputRule(final UUID uuid, final Class<T> inputClass,
			final Predicate<T> activationPredicate, final BiConsumer<T, IDT> collectFunction) {
		final InputDecisionRule<T, IDT> rule = new InputDecisionRule<>(uuid, inputClass, activationPredicate, this,
				new CollectorFunction<T, IDT>() { // NOSONAR
					@Override
					public IDT apply(final T inputData, final ActionPoint<IDT, ?> ap, final Registry reg) {
						final IDT apInputData = ap.get(reg, inputData);
						collectFunction.accept(inputData, apInputData);
						return apInputData;
					}
				});
		inputDecisionRules.add(rule);
		return rule;
	}

	/**
	 * Adds an input rule
	 *
	 * @param uuid
	 * @param inputClass
	 * @param activationPredicate
	 * @param collectFunction
	 * @return
	 */
	public final <T extends FlowData> InputDecisionRule<T, IDT> addInputRule(final UUID uuid, final Class<T> inputClass,
			final Predicate<T> activationPredicate, final Function<T, IDT> collectFunction) {
		final InputDecisionRule<T, IDT> rule = new InputDecisionRule<>(uuid, inputClass, activationPredicate, this,
				(inputData, ap, reg) -> collectFunction.apply(inputData));
		inputDecisionRules.add(rule);
		return rule;
	}

	public void addOutputRule(final List<OutputDecisionRule<ODT, ? extends FlowData>> newRules) {
		outputDecisionRules.addAll(newRules);
	}

	@SafeVarargs
	public final void addOutputRule(final OutputDecisionRule<ODT, ? extends FlowData>... newRules) {
		outputDecisionRules.addAll(Arrays.asList(newRules));
	}

	public List<OutputDecisionRule<ODT, ?>> getOutputRules() {
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
		private IDT inputData;
		private ODT outputData;
		private List<OutputDecisionRule<ODT, ?>> selectedRules;

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
			final List<InputDecisionRule<?, IDT>> inputSelectedRules = inputDecisionRules.stream()
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

		/**
		 * Finds the output rule to apply
		 *
		 * @param orElse
		 *            consumer executed in case of error
		 * @return
		 */
		public List<OutputDecisionRule<?, ?>> selectOutputRules(final Consumer<String> orElse) {
			selectedRules = outputDecisionRules.stream().filter(rule -> rule.getActivationPredicate().test(outputData))
					.collect(toList());
			if (selectedRules.isEmpty()) {
				orElse.accept("NoOutputRule");
			}
			return selectedRules.stream().map(r -> (OutputDecisionRule<?, ?>) r).collect(Collectors.toList());
		}

		/**
		 * Creates the executors that will execute the next action points
		 *
		 * @param registry
		 * @param executorFactory
		 * @return the next action points
		 */
		public <ExecType> Set<ExecType> createExecutions(final ExecutorFactory<ExecType> executorFactory) {
			return selectedRules.stream().map(dr -> dr.createExecutor(outputData, executorFactory))
					.collect(Collectors.toSet());
		}

		public String inputToString() {
			return Objects.toString(untypedInputData);
		}

		public String outputToString() {
			return Objects.toString(outputData);
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
	public ActionPoint<IDT, ODT>.ExecutionSteps invoke(final FlowData inputData) {
		return new ExecutionSteps(inputData);
	}

	public static <IDT extends FlowData, ODT extends FlowData> ActionPoint<IDT, ODT> simple(
			final UUID uuid, final FlowAction<IDT, ODT> action) {
		return new ActionPoint<>(uuid, action);
	}

	public static <IDT extends FlowData> ActionPoint<IDT, ?> terminal(final UUID uuid,
			final FlowAction<IDT, NoData> action) {
		final ActionPoint<IDT, NoData> decisionPoint = new ActionPoint<>(uuid, action);
		decisionPoint.addOutputRule(new OutputDecisionRule<NoData, NoData>(NoData.NO_DATA.uuid(), d -> true,
				FlowActionType.STOP, null, null));
		return decisionPoint;
	}

	public IDT get(final Registry registry, final FlowData data) {
		return registry.get(uuid(), data.getCurrentFlowExecution(), action.getInputDataSupplier());
	}

}
