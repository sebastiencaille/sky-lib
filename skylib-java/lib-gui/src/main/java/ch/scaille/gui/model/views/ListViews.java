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
import java.util.function.Predicate;

public interface ListViews {

	static <U> IListView<U> sortedFiltered(final Comparator<? super U> comparator, final Predicate<? super U> filter) {
		return new ListView<>(comparator, filter);
	}
	
	static <U> IListView<U> sorted(final Comparator<? super U> comparator) {
		return sortedFiltered(comparator, null);
	}

	static <U> IListView<U> filtered(final Predicate<? super U> filter) {
		return sortedFiltered(null, filter);
	}

	static <U extends Comparable<? super U>> IListView<U> sorted() {
		return sorted(Comparator.naturalOrder());
	}
	
	static <U> IListView<U> inherited() {
		return new ListView<>(null, null);
	}

}
