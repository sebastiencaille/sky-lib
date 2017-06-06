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
package org.skymarshall.hmi.mvc;

import org.skymarshall.hmi.mvc.properties.AbstractProperty;

/**
 * Unified access to a component's "property".
 * <p>
 *
 * @author Sebastien Caille
 *
 * @param <T>
 *            the type of the component's property
 */
public interface IComponentBinding<T> {

	public Object getComponent();

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

}
