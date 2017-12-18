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
package org.skymarshall.hmi.model.views;

import java.util.Comparator;

import org.skymarshall.hmi.mvc.IComponentBinding;
import org.skymarshall.hmi.mvc.IComponentLink;
import org.skymarshall.hmi.mvc.properties.AbstractProperty;

/**
 * Comparator that can be used as a component Binding
 *
 * @author scaille
 *
 * @param <DataType>
 * @param <ComparatorPropertyType>
 */
public abstract class BoundComparator<DataType, ComparatorPropertyType> extends AbstractDynamicView<DataType>
		implements IComponentBinding<ComparatorPropertyType>, Comparator<DataType> {

	private ComparatorPropertyType sorterPropertyValue;
	private IListViewOwner<DataType> owner;

	protected abstract int compare(DataType value1, DataType value2, ComparatorPropertyType filter);

	@FunctionalInterface
	public interface BoundComparatorFunc<DataType, SorterPropertyType> {
		int compare(DataType data1, DataType data2, SorterPropertyType propertyValue);
	}

	public static <DataType, SorterPropertyType> BoundComparator<DataType, SorterPropertyType> comparator(
			final BoundComparatorFunc<DataType, SorterPropertyType> comparator) {
		return new BoundComparator<DataType, SorterPropertyType>() {
			@Override
			protected int compare(final DataType value1, final DataType value2, final SorterPropertyType filter) {
				return comparator.compare(value1, value2, filter);
			}
		};
	}

	public BoundComparator() {
	}

	protected ComparatorPropertyType getSorterPropertyValue() {
		return sorterPropertyValue;
	}

	@Override
	public void attach(final IListViewOwner<DataType> viewOwner) {
		this.owner = viewOwner;
	}

	@Override
	public void addComponentValueChangeListener(final IComponentLink<ComparatorPropertyType> link) {
		// Read only
	}

	@Override
	public void removeComponentValueChangeListener() {
		// Read only
	}

	@Override
	public void setComponentValue(final AbstractProperty source, final ComparatorPropertyType value) {
		sorterPropertyValue = value;
		owner.viewUpdated();
	}

	@Override
	public int compare(final DataType value1, final DataType value2) {
		return compare(value1, value2, sorterPropertyValue);
	}

}
