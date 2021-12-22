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
package ch.scaille.gui.mvc.persisters;

import ch.scaille.gui.mvc.properties.IPersister;

/**
 * Allows to persist a property in an attribute of an object
 *
 * @author scaille
 *
 * @param <T> the object type
 * @param <A> the attribute type
 */
public class ObjectProviderPersister<T, A> implements IPersister<A> {

	public interface IObjectProvider<T> {
		T getObject();
	}

	public static class CurrentObjectProvider<T> implements IObjectProvider<T> {

		private T object;

		@Override
		public T getObject() {
			return object;
		}

		public void setObject(final T object) {
			this.object = object;
		}

	}

	private final IObjectProvider<T> target;
	private final IPersisterFactory<T, A> persisterFactory;

	public ObjectProviderPersister(final IObjectProvider<T> target, IPersisterFactory<T, A> persisterFactory) {
		this.target = target;
		this.persisterFactory = persisterFactory;
	}

	@Override
	public A get() {
		return persister().get();
	}

	@Override
	public void set(final A value) {
		persister().set(value);
	}

	private IPersister<A> persister() {
		final T targetObject = target.getObject();
		if (targetObject == null) {
			throw new IllegalStateException("No target object defined");
		}
		return persisterFactory.asPersister(targetObject);
	}

}
