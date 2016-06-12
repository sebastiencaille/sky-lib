package ch.skymarshall.dataflowmgr.engine;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class DecisionRule<ContextType> {

	private final List<Predicate<ContextType>> predicates;
	private final BiFunction<ContextType, ExecutionReport, FlowStatus> action;

	@SafeVarargs
	public DecisionRule(final BiFunction<ContextType, ExecutionReport, FlowStatus> action,
			final Predicate<ContextType>... predicates) {
		this.action = action;
		this.predicates = Arrays.asList(predicates);

	}

	public boolean test(final ContextType input) {
		if (predicates.isEmpty()) {
			return true;
		}
		return predicates.stream().anyMatch(in -> in.test(input));
	}

	public FlowStatus execute(final ContextType context, final ExecutionReport report) {
		return action.apply(context, report);
	}

	/**
	 * Flow action with return value: applies the action and updates the context
	 */
	public static <Ctxt, PrimType, RetType> BiFunction<Ctxt, ExecutionReport, FlowStatus> apply(
			final Function<Ctxt, RetType> action, final BiConsumer<Ctxt, RetType> contextObject) {
		return new BiFunction<Ctxt, ExecutionReport, FlowStatus>() {
			@Override
			public FlowStatus apply(final Ctxt t, final ExecutionReport report) {
				contextObject.accept(t, action.apply(t));
				return FlowStatus.CONTINUE;
			}
		};
	}

	/**
	 * Flow action without return value: applies the action
	 */
	public static <Ctxt> BiFunction<Ctxt, ExecutionReport, FlowStatus> apply(final Consumer<Ctxt> action) {
		return new BiFunction<Ctxt, ExecutionReport, FlowStatus>() {
			@Override
			public FlowStatus apply(final Ctxt t, final ExecutionReport report) {
				action.accept(t);
				return FlowStatus.CONTINUE;
			}
		};
	}

	/**
	 * Flow sink: applies the action and ends the flow, discarding the context
	 */
	public static <Ctxt> BiFunction<Ctxt, ExecutionReport, FlowStatus> sink(final Consumer<Ctxt> action) {
		return new BiFunction<Ctxt, ExecutionReport, FlowStatus>() {
			@Override
			public FlowStatus apply(final Ctxt t, final ExecutionReport report) {
				action.accept(t);
				return FlowStatus.STOP;
			}
		};
	}

	/**
	 * Flow sink: ends the flow, discarding the context
	 */
	public static <Ctxt> BiFunction<Ctxt, ExecutionReport, FlowStatus> sink() {
		return new BiFunction<Ctxt, ExecutionReport, FlowStatus>() {
			@Override
			public FlowStatus apply(final Ctxt t, final ExecutionReport report) {
				return FlowStatus.STOP;
			}
		};
	}

	/**
	 * Flow split: trigger another flow and reintegrate the result
	 */
	public static <Ctxt, Ctxt2> BiFunction<Ctxt, ExecutionReport, FlowStatus> flow(final Flow<Ctxt2> flow,
			final Function<Ctxt, Ctxt2> createFlow, final BiConsumer<Ctxt2, Ctxt> mergeFlow) {
		return new BiFunction<Ctxt, ExecutionReport, FlowStatus>() {
			@Override
			public FlowStatus apply(final Ctxt t, final ExecutionReport report) {
				mergeFlow.accept(flow.build().execute(createFlow.apply(t), report), t);
				return FlowStatus.SPLIT;
			}
		};
	}

	/**
	 * Flow split: trigger another flow and ignore the result
	 */
	public static <Ctxt, Ctxt2> BiFunction<Ctxt, ExecutionReport, FlowStatus> sink(final Flow<Ctxt2> flow,
			final Function<Ctxt, Ctxt2> createFlow) {
		return new BiFunction<Ctxt, ExecutionReport, FlowStatus>() {
			@Override
			public FlowStatus apply(final Ctxt t, final ExecutionReport report) {
				flow.build().execute(createFlow.apply(t), report);
				return FlowStatus.SPLIT;
			}
		};
	}

	/**
	 * Flow switch: switch to another flow
	 */
	public static <Ctxt, Ctxt2> BiFunction<Ctxt, ExecutionReport, FlowStatus> switchFlow(final Flow<Ctxt2> flow,
			final Function<Ctxt, Ctxt2> createFlow) {
		return new BiFunction<Ctxt, ExecutionReport, FlowStatus>() {
			@Override
			public FlowStatus apply(final Ctxt t, final ExecutionReport report) {
				flow.build().execute(createFlow.apply(t), report);
				return FlowStatus.STOP;
			}
		};
	}

	@SuppressWarnings("unchecked")
	public static <Ctxt> DecisionRule<Ctxt>[] either(final Predicate<Ctxt> predicate,
			final BiFunction<Ctxt, ExecutionReport, FlowStatus> ifTrue,
			final BiFunction<Ctxt, ExecutionReport, FlowStatus> ifFalse) {
		return new DecisionRule[] { new DecisionRule<>(ifTrue, predicate),
				new DecisionRule<>(ifFalse, input -> !predicate.test(input)) };
	}

}
