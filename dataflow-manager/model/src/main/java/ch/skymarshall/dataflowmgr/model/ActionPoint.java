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
 * @param <I> type of input flow
 * @param <O> type of output flow
 */
public class ActionPoint<I extends FlowData, O extends FlowData> extends IDData {

	/**
	 * Used to delay activation, eg to make a join on two flows
	 */
	private Predicate<I> activator = (t -> true);

	private final FlowAction<I, O> action;

	private final List<InputDecisionRule<?, I>> inputDecisionRules = new ArrayList<>();
	private final List<OutputDecisionRule<O, ?>> outputDecisionRules = new ArrayList<>();

	public ActionPoint(final UUID uuid, final FlowAction<I, O> action) {
		super(uuid);
		this.action = action;
	}

	public void setActivator(final Predicate<I> activator) {
		this.activator = activator;
	}

	public FlowAction<I, O> getAction() {
		return action;
	}

	/**
	 * Adds an input rule
	 *
	 * @param uuid
	 * @param inputClass
	 * @param activationPredicate
	 * @param collectFunction     function that merges the flow's input into the
	 *                            action point's input
	 * @return
	 */
	public final <T extends FlowData> InputDecisionRule<T, I> addInputRule(final UUID uuid, final Class<T> inputClass,
			final Predicate<T> activationPredicate, final BiConsumer<T, I> collectFunction) {
		final InputDecisionRule<T, I> rule = new InputDecisionRule<>(uuid, inputClass, activationPredicate, this,
				new CollectorFunction<T, I>() { // NOSONAR
					@Override
					public I apply(final T inputData, final ActionPoint<I, ?> ap, final Registry reg) {
						final I apInputData = ap.get(reg, inputData);
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
	public final <T extends FlowData> InputDecisionRule<T, I> addInputRule(final UUID uuid, final Class<T> inputClass,
			final Predicate<T> activationPredicate, final Function<T, I> collectFunction) {
		final InputDecisionRule<T, I> rule = new InputDecisionRule<>(uuid, inputClass, activationPredicate, this,
				(inputData, ap, reg) -> collectFunction.apply(inputData));
		inputDecisionRules.add(rule);
		return rule;
	}

	public void addOutputRule(final List<OutputDecisionRule<O, ? extends FlowData>> newRules) {
		outputDecisionRules.addAll(newRules);
	}

	@SafeVarargs
	public final void addOutputRule(final OutputDecisionRule<O, ? extends FlowData>... newRules) {
		outputDecisionRules.addAll(Arrays.asList(newRules));
	}

	public List<OutputDecisionRule<O, ?>> getOutputRules() {
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
		private I inputData;
		private O outputData;
		private List<OutputDecisionRule<O, ?>> selectedRules;

		private ExecutionSteps(final FlowData untypedInputData) {
			this.untypedInputData = untypedInputData;
		}

		/**
		 * Creates the input data using the input rules
		 *
		 * @param registry
		 * @param orElse   consumer executed in case of error
		 */
		public UUID executeInputRule(final Registry registry, final Consumer<String> orElse) {
			final List<InputDecisionRule<?, I>> inputSelectedRules = inputDecisionRules.stream()
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
		 * @param orElse consumer executed in case of error
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
		public <E> Set<E> createExecutions(final ExecutorFactory<E> executorFactory) {
			return selectedRules.stream().flatMap(dr -> dr.createExecutor(outputData, executorFactory).stream())
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
	public ActionPoint<I, O>.ExecutionSteps createExecution(final FlowData inputData) {
		return new ExecutionSteps(inputData);
	}

	public static <I extends FlowData, O extends FlowData> ActionPoint<I, O> simple(final UUID uuid,
			final FlowAction<I, O> action) {
		return new ActionPoint<>(uuid, action);
	}

	public static <I extends FlowData> ActionPoint<I, ?> terminal(final UUID uuid,
			final FlowAction<I, NoData> action) {
		final ActionPoint<I, NoData> decisionPoint = new ActionPoint<>(uuid, action);
		decisionPoint.addOutputRule(new OutputDecisionRule<NoData, NoData>(NoData.NO_DATA.uuid(), d -> true,
				FlowActionType.STOP, null, null));
		return decisionPoint;
	}

	public I get(final Registry registry, final FlowData data) {
		return registry.get(uuid(), data.getCurrentFlowExecution(), action.getInputDataSupplier());
	}

}