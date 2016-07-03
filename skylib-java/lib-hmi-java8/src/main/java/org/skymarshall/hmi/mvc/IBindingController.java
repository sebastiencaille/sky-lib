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
 * To change the state of the binding.
 * <p>
 * 
 * @author Sebastien Caille
 *
 * @param <ComponentType>
 */
public interface IBindingController<ComponentType> {

    void attach();

    void detach();

    AbstractProperty getProperty();

    void bind(final IComponentBinding<ComponentType> newBinding);

    <T> IBindingController<T> bind(final AbstractLink<ComponentType, T> converter);

    void unbind();
}