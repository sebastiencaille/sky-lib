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
package ch.skymarshall.gui.model;

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

	void mutates();

	void valuesSet(ListEvent<T> event);

	void valuesCleared(ListEvent<T> event);

	void valuesAdded(ListEvent<T> event);

	void valuesRemoved(ListEvent<T> event);

	void editionCancelled(ListEvent<T> event);

	void editionStarted(ListEvent<T> event);

	void editionStopping(ListEvent<T> event);

	void editionStopped(ListEvent<T> event);

	static <U> IListModelListener<U> valuesSet(final Consumer<ListEvent<U>> consumer) {
		return new ListModelAdapter<U>() {
			@Override
			public void valuesSet(final ListEvent<U> event) {
				consumer.accept(event);
			}
		};
	}

	static <U> IListModelListener<U> valuesCleared(final Consumer<ListEvent<U>> consumer) {
		return new ListModelAdapter<U>() {
			@Override
			public void valuesCleared(final ListEvent<U> event) {
				consumer.accept(event);
			}
		};
	}

	static <U> IListModelListener<U> valuesAdded(final Consumer<ListEvent<U>> consumer) {
		return new ListModelAdapter<U>() {
			@Override
			public void valuesAdded(final ListEvent<U> event) {
				consumer.accept(event);
			}
		};
	}

	static <U> IListModelListener<U> valueRemoved(final Consumer<ListEvent<U>> consumer) {
		return new ListModelAdapter<U>() {
			@Override
			public void valuesRemoved(final ListEvent<U> event) {
				consumer.accept(event);
			}
		};
	}

	static <U> IListModelListener<U> editionCancelled(final Consumer<ListEvent<U>> consumer) {
		return new ListModelAdapter<U>() {
			@Override
			public void editionCancelled(final ListEvent<U> event) {
				consumer.accept(event);
			}
		};
	}

	static <U> IListModelListener<U> editionStarted(final Consumer<ListEvent<U>> consumer) {
		return new ListModelAdapter<U>() {
			@Override
			public void editionStarted(final ListEvent<U> event) {
				consumer.accept(event);
			}
		};
	}

	static <U> IListModelListener<U> editionStopping(final Consumer<ListEvent<U>> consumer) {
		return new ListModelAdapter<U>() {
			@Override
			public void editionStopping(final ListEvent<U> event) {
				consumer.accept(event);
			}
		};
	}

	static <U> IListModelListener<U> editionStopped(final Consumer<ListEvent<U>> consumer) {
		return new ListModelAdapter<U>() {
			@Override
			public void editionStopped(final ListEvent<U> event) {
				consumer.accept(event);
			}
		};
	}

}
