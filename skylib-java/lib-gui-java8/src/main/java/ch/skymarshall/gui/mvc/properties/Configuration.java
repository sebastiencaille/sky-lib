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
package ch.skymarshall.gui.mvc.properties;

import java.util.function.Consumer;

import ch.skymarshall.gui.mvc.AutoCommitListener;
import ch.skymarshall.gui.mvc.factories.Persisters;
import ch.skymarshall.gui.mvc.persisters.FieldAccess;
import ch.skymarshall.gui.mvc.properties.AbstractProperty.ErrorNotifier;

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
public class Configuration {

	private Configuration() {
	}

	public static <T, U extends AbstractTypedProperty<T>> Consumer<U> persistent(final IPersister<T> persister) {
		return property -> property.setPersister(persister);
	}

	public static <T, U extends AbstractTypedProperty<T>> Consumer<U> persistent(final Object object,
			final FieldAccess<T> fieldAccess) {
		return persistent(Persisters.from(object, fieldAccess));
	}

	public static <U extends AbstractProperty> Consumer<U> errorNotifier(final ErrorNotifier notifier) {
		return property -> property.setErrorNotifier(notifier);
	}

	public static <U extends AbstractProperty> void autoCommit(final U property) {
		property.addListener(new AutoCommitListener());
	}

}
