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
package ch.skymarshall.gui.model.views;

import java.util.Comparator;
import java.util.function.Function;
import java.util.function.Predicate;

import ch.skymarshall.gui.mvc.properties.ObjectProperty;

public interface ListViews {

	public static <U> IListView<U> sorted(final Comparator<U> comparator) {
		return new ListView<>(comparator, null);
	}

	public static <U, V extends Comparable<V>> IListView<U> sorted(final Function<U, V> comparator) {
		return new ListView<>((o1, o2) -> comparator.apply(o1).compareTo(comparator.apply(o2)), null);
	}

	public static <U extends Comparable<U>> IListView<U> sorted() {
		return new ListView<>((u1, u2) -> u1.compareTo(u2), null);
	}

	public static <U> IListView<U> sortedFiltered(final Comparator<U> comparator, final Predicate<U> filter) {
		return new ListView<>(comparator, filter);
	}

	public static <U> IListView<U> filtered(final Predicate<U> filter) {
		return new ListView<>(null, filter);
	}

	/**
	 * Creates a filter from a property, which object is the filter itself
	 *
	 * @param filter
	 * @return
	 */
	public static <D, F extends Predicate<D>> Predicate<D> filter(final ObjectProperty<F> filter) {
		return new PropertyFilter<>(filter);
	}

	public static <U> IListView<U> inherited() {
		return new ListView<>(null, null);
	}

}
