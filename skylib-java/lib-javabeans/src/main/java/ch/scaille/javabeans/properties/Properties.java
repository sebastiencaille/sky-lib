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

import ch.scaille.javabeans.AutoCommitListener;
import ch.scaille.javabeans.properties.AbstractProperty.ErrorNotifier;

/**
 * To tune the properties.
 * <p>
 * Can be used through static calls, or through instance (such as
 * Properties.of(property).persistent(...)...
 *
 * @author Sebastien Caille
 *
 * @param <T>
 * @param <U>
 */
public class Properties<T, U extends AbstractTypedProperty<T>> {

	private final U property;

	private Properties(final U property) {
		this.property = property;
	}

	public Properties<T, U> persistent(final IPersister<T> persister) {
		persistent(property, persister);
		return this;
	}

	public Properties<T, U> setErrorNotifier(final ErrorNotifier notifier) {
		setErrorNotifier(property, notifier);
		return this;
	}

	public Properties<T, U> autoCommit() {
		autoCommit(property);
		return this;
	}

	public U getProperty() {
		return property;
	}

	public static <T, U extends AbstractTypedProperty<T>> Properties<T, U> of(final U property) {
		return new Properties<>(property);
	}

	public static <T, U extends AbstractTypedProperty<T>> U persistent(final U property,
			final IPersister<T> persister) {
		property.setPersister(persister);
		return property;
	}

	public static <T, U extends AbstractTypedProperty<T>> U setErrorNotifier(final U property,
			final ErrorNotifier notifier) {
		property.setErrorNotifier(notifier);
		return property;
	}

	public static <T, U extends AbstractTypedProperty<T>> U autoCommit(final U property) {
		property.addListener(new AutoCommitListener());
		return property;
	}
}
