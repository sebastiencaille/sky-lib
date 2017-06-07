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
package org.skymarshall.hmi.mvc.converters;

import org.skymarshall.hmi.Utils;
import org.skymarshall.hmi.mvc.HmiErrors.HmiError;

public final class Converters {

	private Converters() {

	}

	public static AbstractObjectConverter<HmiError, String> hmiErrorToString() {
		return new HmiErrorToStringConverter();
	}

	public static AbstractIntConverter<String> intToString() {
		return new IntToStringConverter();
	}

	public static <T> AbstractObjectConverter<T, String> objectToString() {
		return new ObjectToStringConverter<T>();
	}

	public static <T extends Enum<T>> AbstractObjectConverter<T, String> enumToString(
			final Class<T> clazz) {
		return new EnumToStringConverter<T>(clazz);
	}

	public static <T extends Number> ReadOnlyObjectConverter<T, String> numberToSize() {
		return new ReadOnlyObjectConverter<T, String>() {

			@Override
			protected String convertPropertyValueToComponentValue(
					final Number propertyValue) {
				return Utils.toSize(propertyValue);
			}
		};
	}

	public static <T> ReadOnlyObjectConverter<T, Boolean> isNotNull() {
		return new ReadOnlyObjectConverter<T, Boolean>() {
			@Override
			protected Boolean convertPropertyValueToComponentValue(
					final T propertyValue) {
				return propertyValue != null;
			}
		};
	}
}
