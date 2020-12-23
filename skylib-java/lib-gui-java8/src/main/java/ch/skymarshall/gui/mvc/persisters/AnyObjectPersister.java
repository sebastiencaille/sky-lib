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
package ch.skymarshall.gui.mvc.persisters;

import ch.skymarshall.gui.mvc.properties.IPersister;

public class AnyObjectPersister<T> implements IPersister<T> {

	private final FieldAccess<T> fieldAccess;
	private Object target;

	public AnyObjectPersister(final FieldAccess<T> fieldAccess) {
		this.fieldAccess = fieldAccess;
	}

	public void setTarget(final Object target) {
		this.target = target;
	}

	@Override
	public T get() {
		if (target == null) {
			throw new IllegalStateException("No target object defined");
		}
		return fieldAccess.get(target);
	}

	@Override
	public void set(final T value) {
		if (target == null) {
			throw new IllegalStateException("No target object defined");
		}
		fieldAccess.set(target, value);
	}

}
