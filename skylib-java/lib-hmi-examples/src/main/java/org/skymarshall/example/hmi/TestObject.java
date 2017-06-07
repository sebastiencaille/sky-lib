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
package org.skymarshall.example.hmi;

import org.skymarshall.hmi.HmiObject;
import org.skymarshall.hmi.mvc.converters.AbstractObjectConverter;
import org.skymarshall.hmi.mvc.converters.Converters;

@HmiObject
public class TestObject {

	public TestObject(final String string, final int i) {
		aFirstValue = string;
		aSecondValue = i;
	}

	public String aFirstValue;

	public int aSecondValue;

	@Override
	public String toString() {
		return aFirstValue + " / " + aSecondValue;
	}

	public static AbstractObjectConverter<TestObject, String> testObjectToString() {
		return Converters.writeOnly(o -> o.aFirstValue != null ? o.aFirstValue : "");
	}
}
