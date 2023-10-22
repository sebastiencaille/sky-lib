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
package ch.scaille.javabeans.properties;

import static ch.scaille.javabeans.persisters.Persisters.of;
import static ch.scaille.javabeans.persisters.Persisters.persister;

import java.util.function.Consumer;

import ch.scaille.javabeans.AutoCommitListener;
import ch.scaille.javabeans.persisters.IPersisterFactory;
import ch.scaille.javabeans.persisters.ObjectProviderPersister.IObjectProvider;
import ch.scaille.javabeans.properties.AbstractProperty.ErrorNotifier;

/**
 * To tune the properties.
 * <p>
 * Can be used through static calls, or through instance (such as
 * Properties.of(property).persistent(...)...
 *
 * @author Sebastien Caille
 */
public class Configuration {

	private Configuration() {
	}

	public static <A, P extends AbstractTypedProperty<A>> Consumer<P> persistent(final IPersister<A> persister) {
		return property -> property.setPersister(persister);
	}

	/**
	 * 
	 * @param <T> the type of the persisted bean
	 * @param <A> the type of the persisted attribute
	 * @param <P> the type of the attribute Property
	 */
	public static <T, A, P extends AbstractTypedProperty<A>> Consumer<P> persistent(IObjectProvider<T> object,
			final IPersisterFactory<T, A> persisterFactory) {
		return persistent(persister(object, persisterFactory));
	}

	public static <T, A, P extends AbstractTypedProperty<A>> Consumer<P> persistent(T object,
			final IPersisterFactory<T, A> persisterFactory) {
		return persistent(of(object), persisterFactory);
	}

	public static <U extends AbstractProperty> Consumer<U> errorNotifier(final ErrorNotifier notifier) {
		return property -> property.setErrorNotifier(notifier);
	}

	public static <U extends AbstractProperty> void autoCommit(final U property) {
		property.addListener(new AutoCommitListener());
	}

}
