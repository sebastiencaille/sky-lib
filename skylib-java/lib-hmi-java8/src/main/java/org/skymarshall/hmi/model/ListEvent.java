/*******************************************************************************
 * Copyright (c) 2017 Sebastien Caille.
 *  All rights reserved.
 * 
 *  Redistribution and use in source and binary forms are permitted
 *  provided that the above copyright notice and this paragraph are
 *  duplicated in all such forms and that any documentation,
 *  advertising materials, and other materials related to such
 *  distribution and use acknowledge that the software was developed
 *  by Sebastien Caille.  The name of Sebastien Caille may not be used to endorse or promote products derived
 *  from this software without specific prior written permission.
 *  THIS SOFTWARE IS PROVIDED ``AS IS'' AND WITHOUT ANY EXPRESS OR
 *  IMPLIED WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 ******************************************************************************/
package org.skymarshall.hmi.model;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Event on dynamic list.
 * <p>
 * 
 * @author Sebastien Caille
 * 
 * @param <T>
 */
public class ListEvent<T> {

	private final ListModel<T>	source;

	private final List<T>				objects;

	public ListEvent(final ListModel<T> source) {
		this.source = source;
		objects = null;
	}

	public ListEvent(final ListModel<T> source, final T object) {
		this.source = source;
		this.objects = Collections.singletonList(object);
	}

	public ListEvent(final ListModel<T> source, final List<T> objects) {
		this.source = source;
		this.objects = objects;
	}

	public ListModel<T> getSource() {
		return source;
	}

	public Collection<T> getObjects() {
		return objects;
	}

	public T getObject() {
		if (objects.size() > 1) {
			throw new IllegalStateException("Event has more than one object");
		}
		return objects.get(0);
	}

}
