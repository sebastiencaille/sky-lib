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
package ch.scaille.example.gui.controller.impl;

import java.util.Collections;
import java.util.List;

import ch.scaille.javabeans.converters.IConverter;
import ch.scaille.util.helpers.JavaExt;

public class DynamicListContentConverter implements IConverter<String, List<String>> {

	@Override
	public List<String> convertPropertyValueToComponentValue(final String propertyValue) {

		if (propertyValue == null) {
			return Collections.emptyList();
		}

		switch (propertyValue) {
		case "A":
			return List.of("A", "B", "C");
		case "B":
			return List.of("B", "C", "D");
		case "C":
			return List.of("C", "D", "E");
		default:
			return Collections.emptyList();
		}
	}

	@Override
	public String convertComponentValueToPropertyValue(final List<String> componentValue) {
		throw JavaExt.notImplemented();
	}

}
