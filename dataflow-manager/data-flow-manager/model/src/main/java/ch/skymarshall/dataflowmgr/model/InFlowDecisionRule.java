package ch.skymarshall.dataflowmgr.model;

import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

/**
 * Transforms a data and send it to the next action
 *
 * @author scaille
 *
 * @param <InFlowType>
 * @param <InActionPointType>
 */
public class InFlowDecisionRule<InFlowType extends FlowData, InActionPointType extends FlowData> extends IDData {

	@FunctionalInterface
	public static interface CollectorFunction<InputDataType, DecisionPointDataType extends FlowData> {
		DecisionPointDataType apply(InputDataType inputData, ActionPoint<DecisionPointDataType, ?> ap, Registry reg);
	}

	private final Predicate<InFlowType> activationPredicate;
	private final ActionPoint<InActionPointType, ?> actionPointToExecute;
	private final CollectorFunction<InFlowType, InActionPointType> collectFunction;
	private final Class<InFlowType> inputClass;

	public static <InFlow extends FlowData, ActPointInType extends FlowData> InFlowDecisionRule<InFlow, ActPointInType> input(
			final UUID uuid, final Class<InFlow> inputClass, final ActionPoint<ActPointInType, ?> actionPoint,
			final CollectorFunction<InFlow, ActPointInType> fillFunction, final Predicate<InFlow> activationPredicate) {
		return new InFlowDecisionRule<>(uuid, inputClass, actionPoint, fillFunction, activationPredicate);
	}

	/**
	 * Fills the input class with the incoming data using the fillFunction
	 *
	 * @param uuid
	 * @param inputClass
	 * @param actionPoint
	 * @param fillFunction
	 * @param activationPredicate
	 * @return
	 */
	public static <InFlow extends FlowData, InActPointType extends FlowData> InFlowDecisionRule<InFlow, InActPointType> input(
			final UUID uuid, final Class<InFlow> inputClass, final ActionPoint<InActPointType, ?> actionPoint,
			final BiConsumer<InFlow, InActPointType> fillFunction, final Predicate<InFlow> activationPredicate) {
		return new InFlowDecisionRule<>(uuid, inputClass, actionPoint, new CollectorFunction<InFlow, InActPointType>() {
			@Override
			public InActPointType apply(final InFlow incomingData, final ActionPoint<InActPointType, ?> ap,
					final Registry reg) {
				final InActPointType in = ap.get(reg, incomingData);
				fillFunction.accept(incomingData, in);
				return in;
			}
		}, activationPredicate);
	}

	protected InFlowDecisionRule(final UUID uuid, final Class<InFlowType> inputClass,
			final ActionPoint<InActionPointType, ?> ap,
			final CollectorFunction<InFlowType, InActionPointType> collectFunction,
			final Predicate<InFlowType> activationPredicate) {
		super(uuid);
		this.inputClass = inputClass;
		this.activationPredicate = activationPredicate;
		this.actionPointToExecute = ap;
		this.collectFunction = collectFunction;
	}

	public ActionPoint<?, ?> getActionPointToExecute() {
		return actionPointToExecute;
	}

	protected InActionPointType convertData(final FlowData inFlow, final Registry registry) {
		final InActionPointType apValue = collectFunction.apply(inputClass.cast(inFlow), actionPointToExecute,
				registry);
		apValue.setContext(inFlow, actionPointToExecute.uuid());
		return apValue;
	}

	public boolean matches(final FlowData inputData) {
		return inputClass.isInstance(inputData) && activationPredicate.test(inputClass.cast(inputData));
	}

}
