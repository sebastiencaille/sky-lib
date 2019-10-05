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
package ch.skymarshall.example.gui;

import ch.skymarshall.gui.GuiObject;
import ch.skymarshall.gui.mvc.converters.Converters;
import ch.skymarshall.gui.mvc.converters.IConverter;

@GuiObject
public class TestObject {

	public TestObject(final String afirstValue, final int i) {
		this.aFirstValue = afirstValue;
		aSecondValue = i;
	}

	private String aFirstValue;

	private int aSecondValue;

	public String getAFirstValue() {
		return aFirstValue;
	}

	public void setAFirstValue(final String aFirstValue) {
		this.aFirstValue = aFirstValue;
	}

	public int getASecondValue() {
		return aSecondValue;
	}

	public void setASecondValue(final int aSecondValue) {
		this.aSecondValue = aSecondValue;
	}

	@Override
	public String toString() {
		return aFirstValue + " / " + aSecondValue;
	}

	public static IConverter<TestObject, String> testObjectToString() {
		return Converters.writeOnly(o -> o.aFirstValue != null ? o.aFirstValue : "");
	}
}
