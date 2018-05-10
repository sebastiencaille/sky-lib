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
package org.skymarshall.util.dao.metadata;

import java.lang.reflect.Method;

/**
 * This class allows accessing a Read Only attribute
 * 
 * @author Sebastien Caille
 *
 * @param <T>
 */
public class ReadOnlyAttribute<T> extends GetSetAttribute<T> {

	public ReadOnlyAttribute(final String name, final Method getter) {
		super(name, getter, null);
	}

	@Override
	public Class<?> getDeclaringType() {
		return getter.getDeclaringClass();
	}

	@Override
	public void setValueOf(final T to, final Object value) {
		throw new IllegalStateException("Attribute " + name + " is read only");
	}

	@Override
	public String toString() {
		return name + '(' + type.getName() + ", ReadOnly)";
	}

	@Override
	public boolean isReadOnly() {
		return true;
	}
}
