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

import java.util.function.BiPredicate;
import java.util.function.Predicate;

import org.skymarshall.hmi.mvc.IComponentBinding;
import org.skymarshall.hmi.mvc.IComponentLink;
import org.skymarshall.hmi.mvc.properties.AbstractProperty;

/**
 * Filter that can be used as a component Binding
 *
 * @author scaille
 *
 * @param <DataType>
 * @param <FilterPropertyType>
 */
public abstract class BoundFilter<DataType, FilterPropertyType> extends AbstractDynamicView<DataType>
		implements IComponentBinding<FilterPropertyType>, Predicate<DataType> {

	private FilterPropertyType filterPropertyValue;
	private IListViewOwner<DataType> owner;

	protected abstract boolean accept(DataType value, FilterPropertyType filter);

	public static <DataType, FilterObjectType> BoundFilter<DataType, FilterObjectType> filter(
			final BiPredicate<DataType, FilterObjectType> consumer) {
		return new BoundFilter<DataType, FilterObjectType>() {

			@Override
			protected boolean accept(final DataType value, final FilterObjectType filter) {
				return consumer.test(value, filter);
			}
		};
	}

	public BoundFilter() {
	}

	protected FilterPropertyType getFilterPropertyValue() {
		return filterPropertyValue;
	}

	@Override
	public void attach(final IListViewOwner<DataType> viewOwner) {
		this.owner = viewOwner;
	}

	@Override
	public void addComponentValueChangeListener(final IComponentLink<FilterPropertyType> link) {
		// Read only
	}

	@Override
	public void removeComponentValueChangeListener() {
		// Read only
	}

	@Override
	public void setComponentValue(final AbstractProperty source, final FilterPropertyType value) {
		filterPropertyValue = value;
		owner.viewUpdated();
	}

	@Override
	public boolean test(final DataType value) {
		if (filterPropertyValue == null) {
			// not yet initialized
			return false;
		}
		return accept(value, filterPropertyValue);
	}

}
