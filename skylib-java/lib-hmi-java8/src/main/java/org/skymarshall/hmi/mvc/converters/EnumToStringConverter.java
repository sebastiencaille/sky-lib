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
package org.skymarshall.hmi.mvc.converters;

public class EnumToStringConverter<E extends Enum<E>> extends AbstractObjectConverter<E, String> {
	private final Class<E> clazz;

	public EnumToStringConverter(final Class<E> clazz) {
		this.clazz = clazz;
	}

	@Override
	public E convertComponentValueToPropertyValue(final String componentObject) {
		for (final E value : clazz.getEnumConstants()) {
			if (value.toString().equals(componentObject)) {
				return value;
			}
		}
		return null;
	}

	@Override
	public String convertPropertyValueToComponentValue(final E value) {
		if (value == null) {
			return "";
		}
		return value.toString();
	}

}
