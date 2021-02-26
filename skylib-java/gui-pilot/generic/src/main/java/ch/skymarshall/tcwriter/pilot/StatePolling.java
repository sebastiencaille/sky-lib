package ch.skymarshall.tcwriter.pilot;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class StatePolling<C, V> extends Polling<C, V> {

	public StatePolling(final PollingFunction<C, V> pollingFunction) {
		super(null, pollingFunction);
	}

	public StatePolling(final Predicate<C> precondition, final PollingFunction<C, V> pollingFunction) {
		super(precondition, pollingFunction);
	}

	@Override
	public Predicate<C> getPrecondition(final AbstractGuiComponent<?, C> guiComponent) {
		if (super.getPrecondition(guiComponent) != null) {
			return super.getPrecondition(guiComponent);
		}
		return guiComponent::canCheck;
	}

	/**
	 * Make polling successful if condition is accepted
	 *
	 * @param <C>
	 * @param <V>
	 * @param reason
	 * @return
	 */
	public static <C> Polling<C, Boolean> satisfies(final Predicate<C> predicate) {
		return new Polling<>(null, c -> {
			if (!predicate.test(c)) {
				return new PollingResult<>(null, new RuntimeException("Condition not met"));
			}
			return PollingResult.success();
		});
	}

	/**
	 * Make polling successful if assert is successful
	 *
	 * @param <C>
	 * @param <V>
	 * @param reason
	 * @return
	 */
	public static <C> Polling<C, Boolean> assertion(final Consumer<C> assertion) {
		return new Polling<>(null, c -> {
			try {
				assertion.accept(c);
				return PollingResult.success();
			} catch (final AssertionError e) {
				return new PollingResult<>(null, e);
			}
		});
	}

}
