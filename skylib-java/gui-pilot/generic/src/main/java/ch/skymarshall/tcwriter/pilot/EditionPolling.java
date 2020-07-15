package ch.skymarshall.tcwriter.pilot;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class EditionPolling<C, V> extends Polling<C, V> {

	public EditionPolling(final PollingFunction<C, V> pollingFunction) {
		super(null, pollingFunction);
	}

	public EditionPolling(final Predicate<C> precondition, final PollingFunction<C, V> pollingFunction) {
		super(precondition, pollingFunction);
	}

	@Override
	public Predicate<C> getPrecondition(final AbstractGuiComponent<?, C> guiComponent) {
		return guiComponent::canEdit;
	}

	/**
	 * Make polling successful after action is applied
	 *
	 * @param <C>
	 * @param action
	 * @return
	 */
	public static <C> Polling<C, Boolean> action(final Consumer<C> action) {
		return new EditionPolling<>(null, c -> {
			action.accept(c);
			return PollingResult.success();
		});
	}

}
