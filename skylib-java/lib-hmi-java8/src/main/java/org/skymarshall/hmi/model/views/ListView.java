/*******************************************************************************
 * Copyright (c) 2013 Sebastien Caille.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms are permitted
 * provided that the above copyright notice and this paragraph are
 * duplicated in all such forms and that any documentation,
 * advertising materials, and other materials related to such
 * distribution and use acknowledge that the software was developed
 * by Sebastien Caille.  The name of Sebastien Caille may not be used to endorse or promote products derived
 * from this software without specific prior written permission.
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND WITHOUT ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 ******************************************************************************/
package org.skymarshall.hmi.model.views;

import java.io.Serializable;
import java.util.Comparator;
import java.util.function.Predicate;

/**
 * Default IListView implementation.
 * <p>
 * The comparator is mandatory, because log(n) access to data requires sorted
 * data.
 *
 * @author Sebastien Caille
 *
 * @param <T>
 */
public class ListView<T> implements IListView<T>, Serializable {

	private static final long serialVersionUID = -4696996416566266010L;

	private IListView<T> parentView;
	protected final Predicate<T> filter;
	protected final Comparator<T> comparator;

	/**
	 * Creates a list view using a comparator and a filter
	 *
	 * @param comparator
	 * @param filter
	 */
	public ListView(final Comparator<T> comparator, final Predicate<T> filter) {
		this.filter = filter;
		this.comparator = comparator;
	}

	@Override
	public boolean accept(final T object) {
		return filter == null || filter.test(object);
	}

	@Override
	public int compare(final T o1, final T o2) {
		final int compare;
		if (comparator == null && parentView == null) {
			throw new IllegalStateException(
					this
							+ ": you must either set a comparator or override this method");
		} else if (comparator != null) {
			compare = comparator.compare(o1, o2);
		} else {
			compare = 0;
		}
		if (compare == 0 && parentView != null) {
			return parentView.compare(o1, o2);
		}
		return compare;
	}

	@Override
	public void attach(final IListViewOwner<T> owner) {
		parentView = owner.getParentView();
		if (filter instanceof AbstractDynamicFilter) {
			((AbstractDynamicFilter<T>) filter).attach(owner);
		}
	}

	@Override
	public void detach(final IListViewOwner<T> owner) {
		// no op
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + ":[comparator=" + comparator
				+ ", filter=" + filter + ']';
	}

}
