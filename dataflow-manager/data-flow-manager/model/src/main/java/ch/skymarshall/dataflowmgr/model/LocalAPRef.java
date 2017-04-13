package ch.skymarshall.dataflowmgr.model;

public class LocalAPRef<T extends FlowData> implements ActionPointReference<T> {

	private final ActionPoint<?, ?> actionPoint;

	private LocalAPRef(final ActionPoint<?, ?> actionPoint) {
		this.actionPoint = actionPoint;
	}

	public ActionPoint<?, ?> getActionPoint() {
		return actionPoint;
	}

	public static <T extends FlowData> ActionPointReference<T> local(final ActionPoint<T, ?> ap) {
		return new LocalAPRef<>(ap);
	}

	public static <T extends FlowData> ActionPointReference<T> local(final InFlowDecisionRule<T, ?> in) {
		return new LocalAPRef<>(in.getActionPointToExecute());
	}

	public static <T extends FlowData> ActionPoint<?, ?>.ExecutionSteps invoke(final ActionPointReference<T> nextDp,
			final T nextData) {
		return LocalAPRef.class.cast(nextDp).getActionPoint().invoke(nextData);
	}
}
