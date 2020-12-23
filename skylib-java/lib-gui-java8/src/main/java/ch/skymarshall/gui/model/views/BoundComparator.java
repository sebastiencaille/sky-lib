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

import ch.skymarshall.gui.mvc.IComponentBinding;
import ch.skymarshall.gui.mvc.IComponentLink;
import ch.skymarshall.gui.mvc.properties.AbstractProperty;

/**
 * Comparator that can be used as a component Binding
 *
 * @author scaille
 *
 * @param <D> type of the compared data
 * @param <C> type of the comparator
 */
public abstract class BoundComparator<D, C> extends AbstractDynamicView<D>
		implements IComponentBinding<C>, Comparator<D> {

	private C sorterPropertyValue;
	private IListViewOwner<D> owner;

	protected abstract int compare(D value1, D value2, C filter);

	/**
	 * @param <D> Type of the sorted data
	 * @param <C> Type of the comparator property
	 */
	@FunctionalInterface
	public interface BoundComparatorFunc<D, C> {
		int compare(D data1, D data2, C propertyValue);
	}

	/**
	 * @param <D>        Type of the sorted data
	 * @param <C>        Type of the comparator property
	 * @param comparator
	 * @return
	 */
	public static <D, C> BoundComparator<D, C> comparator(final BoundComparatorFunc<D, C> comparator) {
		return new BoundComparator<D, C>() {
			@Override
			protected int compare(final D value1, final D value2, final C filter) {
				return comparator.compare(value1, value2, filter);
			}
		};
	}

	public BoundComparator() {
	}

	protected C getSorterPropertyValue() {
		return sorterPropertyValue;
	}

	@Override
	public void attach(final IListViewOwner<D> viewOwner) {
		this.owner = viewOwner;
	}

	@Override
	public void addComponentValueChangeListener(final IComponentLink<C> link) {
		// Read only
	}

	@Override
	public void removeComponentValueChangeListener() {
		// Read only
	}

	@Override
	public void setComponentValue(final AbstractProperty source, final C value) {
		sorterPropertyValue = value;
		owner.viewUpdated();
	}

	@Override
	public int compare(final D value1, final D value2) {
		return compare(value1, value2, sorterPropertyValue);
	}

}
