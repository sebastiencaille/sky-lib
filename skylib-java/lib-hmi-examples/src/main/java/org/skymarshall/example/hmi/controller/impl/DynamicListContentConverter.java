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
package org.skymarshall.example.hmi.controller.impl;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.skymarshall.hmi.mvc.converters.AbstractObjectConverter;
import org.skymarshall.hmi.mvc.converters.ConversionException;

public class DynamicListContentConverter extends AbstractObjectConverter<String, List<String>> {

	@Override
	public List<String> convertPropertyValueToComponentValue(final String propertyValue) {

		if (propertyValue == null) {
			return Collections.emptyList();
		}

		switch (propertyValue) {
		case "A":
			return Arrays.asList("A", "B", "C");
		case "B":
			return Arrays.asList("B", "C", "D");
		case "C":
			return Arrays.asList("C", "D", "E");
		}
		return Collections.emptyList();
	}

	@Override
	public String convertComponentValueToPropertyValue(final List<String> componentValue) throws ConversionException {
		return null;
	}

}
