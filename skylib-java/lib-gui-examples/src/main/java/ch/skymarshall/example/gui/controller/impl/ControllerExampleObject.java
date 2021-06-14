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
package ch.skymarshall.example.gui.controller.impl;

import ch.skymarshall.annotations.GuiObject;
import ch.skymarshall.example.gui.TestObject;
import jakarta.validation.constraints.NotBlank;

@GuiObject
public class ControllerExampleObject {

	private boolean booleanProp;
	private int intProp;
	private String stringProp;
	private TestObject testObjectProp;

	public boolean isBooleanProp() {
		return booleanProp;
	}

	public void setBooleanProp(final boolean booleanProp) {
		this.booleanProp = booleanProp;
	}

	public int getIntProp() {
		return intProp;
	}

	public void setIntProp(final int intProp) {
		this.intProp = intProp;
	}

	@NotBlank
	public String getStringProp() {
		return stringProp;
	}

	public void setStringProp(final String stringProp) {
		this.stringProp = stringProp;
	}

	public TestObject getTestObjectProp() {
		return testObjectProp;
	}

	public void setTestObjectProp(final TestObject testObjectProp) {
		this.testObjectProp = testObjectProp;
	}

}
