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
package ch.scaille.gui.model.views;

import java.util.Comparator;
import java.util.function.Function;
import java.util.function.Predicate;

import ch.scaille.gui.mvc.properties.ObjectProperty;

public interface ListViews {

	static <U> IListView<U> sorted(final Comparator<U> comparator) {
		return new ListView<>(comparator, null);
	}

	static <U extends V, V extends Comparable<V>> IListView<U> sorted(final Function<U, V> comparator) {
		return new ListView<>(Comparator.comparing(comparator::apply), null);
	}

	static <U extends V, V extends Comparable<V>> IListView<U> sorted() {
		return new ListView<>(Comparator.naturalOrder(), (Predicate<U>) null);
	}

	static <U> IListView<U> sortedFiltered(final Comparator<U> comparator, final Predicate<U> filter) {
		return new ListView<>(comparator, filter);
	}

	static <U> IListView<U> filtered(final Predicate<U> filter) {
		return new ListView<>(null, filter);
	}

	/**
	 * Creates a filter from a property, which object is the filter itself
	 *
	 * @param filter
	 * @return
	 */
	static <D, F extends Predicate<D>> Predicate<D> filter(final ObjectProperty<F> filter) {
		return new PropertyFilter<>(filter);
	}

	static <U> IListView<U> inherited() {
		return new ListView<>(null, null);
	}

}
