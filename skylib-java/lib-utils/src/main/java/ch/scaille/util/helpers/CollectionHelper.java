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
package ch.scaille.util.helpers;

import java.util.Collection;
import java.util.stream.Collectors;

public interface CollectionHelper {

	public static void checkContent(final Collection<?> collection, final Class<?> clazz) {
		if (!collection.stream().allMatch(clazz::isInstance)) {
			throw new IllegalArgumentException("Collection has an instances of " + collection.stream()
					.map(Object::getClass).filter(c -> !clazz.isAssignableFrom(c)).collect(Collectors.toSet())
					+ ", which are not " + clazz);
		}
	}

}
