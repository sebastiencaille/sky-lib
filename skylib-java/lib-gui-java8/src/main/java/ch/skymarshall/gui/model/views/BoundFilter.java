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

import java.util.function.BiPredicate;
import java.util.function.Predicate;

import ch.skymarshall.gui.mvc.IComponentBinding;
import ch.skymarshall.gui.mvc.IComponentLink;
import ch.skymarshall.gui.mvc.properties.AbstractProperty;

/**
 * Filter that can be used as a component Binding
 *
 * @author scaille
 *
 * @param <D> the type of the filtered data
 * @param <F> the type of the property that filters the data
 */
public abstract class BoundFilter<D, F> extends AbstractDynamicView<D> implements IComponentBinding<F>, Predicate<D> {

	private F filterPropertyValue;
	private IListViewOwner<D> owner;

	protected abstract boolean accept(D value, F filter);

	/**
	 * * @param <D> the type of the filtered data
	 * 
	 * @param <F>      the type of the property that filters the data
	 * @param consumer
	 * @return
	 */
	public static <D, F> BoundFilter<D, F> filter(final BiPredicate<D, F> consumer) {
		return new BoundFilter<D, F>() {

			@Override
			protected boolean accept(final D value, final F filter) {
				return consumer.test(value, filter);
			}
		};
	}

	protected BoundFilter() {
	}

	protected F getFilterPropertyValue() {
		return filterPropertyValue;
	}

	@Override
	public void attach(final IListViewOwner<D> viewOwner) {
		this.owner = viewOwner;
	}

	@Override
	public void addComponentValueChangeListener(final IComponentLink<F> link) {
		// Read only
	}

	@Override
	public void removeComponentValueChangeListener() {
		// Read only
	}

	@Override
	public void setComponentValue(final AbstractProperty source, final F value) {
		filterPropertyValue = value;
		owner.viewUpdated();
	}

	@Override
	public boolean test(final D value) {
		if (filterPropertyValue == null) {
			// not yet initialized
			return false;
		}
		return accept(value, filterPropertyValue);
	}

}
