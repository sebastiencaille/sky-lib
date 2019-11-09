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
package ch.skymarshall.gui.mvc;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;

import ch.skymarshall.gui.mvc.properties.AbstractProperty;

/**
 * Unified access to a component's "property".
 * <p>
 *
 * @author Sebastien Caille
 *
 * @param <T> the type of the component's property
 */
public interface IComponentBinding<T> {

	/**
	 * Called when bound to a link, so the component binding can hook to the
	 * component and forward it's content to the property
	 */
	public void addComponentValueChangeListener(final IComponentLink<T> link);

	/**
	 *
	 * @param source
	 * @param value
	 */
	public void setComponentValue(final AbstractProperty source, final T value);

	@FunctionalInterface
	public static interface ValueSetter<C, T> {
		void setComponentValue(C component, AbstractProperty property, T value);
	}

	public static <C, T, L> IComponentBinding<T> component(final C component,
			final BiFunction<C, IComponentLink<T>, L> addValueChangeListener,
			final BiConsumer<C, L> removeValueChangeListener, final ValueSetter<C, T> setComponentValue) {
		return new IComponentBinding<T>() {

			private L listener;

			@Override
			public void addComponentValueChangeListener(final IComponentLink<T> link) {
				listener = addValueChangeListener.apply(component, link);
			}

			@Override
			public void setComponentValue(final AbstractProperty source, final T value) {
				setComponentValue.setComponentValue(component, source, value);
			}

			@Override
			public void removeComponentValueChangeListener() {
				if (listener != null) {
					removeValueChangeListener.accept(component, listener);
				}
			}
		};
	}

	/**
	 *
	 * @param setComponentValue (source, value)
	 * @return
	 */
	public static <T> IComponentBinding<T> wo(final BiConsumer<AbstractProperty, T> setComponentValue) {
		return new IComponentBinding<T>() {

			@Override
			public void addComponentValueChangeListener(final IComponentLink<T> link) {
				// component value never read
			}

			@Override
			public void removeComponentValueChangeListener() {
				// component value never read
			}

			@Override
			public void setComponentValue(final AbstractProperty source, final T value) {
				setComponentValue.accept(source, value);
			}

		};
	}

	/**
	 *
	 * @param setComponentValue (source, value)
	 * @return
	 */
	public static <C, T> IComponentBinding<T> wo(final C component, final ValueSetter<C, T> setComponentValue) {
		return new IComponentBinding<T>() {

			@Override
			public void addComponentValueChangeListener(final IComponentLink<T> link) {
				// component value never read
			}

			@Override
			public void removeComponentValueChangeListener() {
				// component value never read
			}

			@Override
			public void setComponentValue(final AbstractProperty source, final T value) {
				setComponentValue.setComponentValue(component, source, value);
			}

		};
	}

	public void removeComponentValueChangeListener();
}
