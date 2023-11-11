package ch.scaille.gui.model;

import java.util.EventListener;
import java.util.function.Consumer;

/**
 * Listener over dynamic list events.
 * <p>
 *
 * @author Sebastien Caille
 *
 * @param <T>
 */
public interface IListModelListener<T> extends EventListener {

	default void mutates() {
		// noop
	}

	default void mutated() {
		// noop
	}

	default void valuesSet(ListEvent<T> event) {
		// noop
	}

	default void valuesCleared(ListEvent<T> event) {
		// noop
	}

	default void valuesAdded(ListEvent<T> event) {
		// noop
	}

	default void valuesRemoved(ListEvent<T> event) {
		// noop
	}

	default void editionCancelled(ListEvent<T> event) {
		// noop
	}

	default void editionStarted(ListEvent<T> event) {
		// noop
	}

	default void editionStopping(ListEvent<T> event) {
		// noop
	}

	default void editionStopped(ListEvent<T> event) {
		// noop
	}

	static <U> IListModelListener<U> valuesSet(final Consumer<ListEvent<U>> consumer) {
		return new IListModelListener<>() {
			@Override
			public void valuesSet(final ListEvent<U> event) {
				consumer.accept(event);
			}
		};
	}

	static <U> IListModelListener<U> valuesCleared(final Consumer<ListEvent<U>> consumer) {
		return new IListModelListener<>() {
			@Override
			public void valuesCleared(final ListEvent<U> event) {
				consumer.accept(event);
			}
		};
	}

	static <U> IListModelListener<U> valuesAdded(final Consumer<ListEvent<U>> consumer) {
		return new IListModelListener<>() {
			@Override
			public void valuesAdded(final ListEvent<U> event) {
				consumer.accept(event);
			}
		};
	}

	static <U> IListModelListener<U> valueRemoved(final Consumer<ListEvent<U>> consumer) {
		return new IListModelListener<>() {
			@Override
			public void valuesRemoved(final ListEvent<U> event) {
				consumer.accept(event);
			}
		};
	}

	static <U> IListModelListener<U> editionCancelled(final Consumer<ListEvent<U>> consumer) {
		return new IListModelListener<>() {
			@Override
			public void editionCancelled(final ListEvent<U> event) {
				consumer.accept(event);
			}
		};
	}

	static <U> IListModelListener<U> editionStarted(final Consumer<ListEvent<U>> consumer) {
		return new IListModelListener<>() {
			@Override
			public void editionStarted(final ListEvent<U> event) {
				consumer.accept(event);
			}
		};
	}

	static <U> IListModelListener<U> editionStopping(final Consumer<ListEvent<U>> consumer) {
		return new IListModelListener<>() {
			@Override
			public void editionStopping(final ListEvent<U> event) {
				consumer.accept(event);
			}
		};
	}

	static <U> IListModelListener<U> editionStopped(final Consumer<ListEvent<U>> consumer) {
		return new IListModelListener<>() {
			@Override
			public void editionStopped(final ListEvent<U> event) {
				consumer.accept(event);
			}
		};
	}

}
