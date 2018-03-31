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

import java.util.function.Supplier;

public interface IConverter<PropertyType, ComponentType> {

	ComponentType convertPropertyValueToComponentValue(final PropertyType propertyValue);

	PropertyType convertComponentValueToPropertyValue(ComponentType componentValue) throws ConversionException;

	public static <C> IConverter<Boolean, C> either(final Supplier<C> either, final Supplier<C> or) {
		return new IConverter<Boolean, C>() {

			@Override
			public C convertPropertyValueToComponentValue(final Boolean propertyValue) {
				if (propertyValue) {
					return either.get();
				}
				return or.get();
			}

			/**
			 * @throws ConversionException exception thrown when a conversion error occurs
			 */
			@Override
			public Boolean convertComponentValueToPropertyValue(final C componentValue) throws ConversionException {
				throw new IllegalStateException("Write only converter");
			}

		};
	}

}
