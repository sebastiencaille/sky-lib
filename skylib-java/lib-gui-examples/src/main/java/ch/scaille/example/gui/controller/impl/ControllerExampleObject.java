package ch.scaille.example.gui.controller.impl;

import ch.scaille.annotations.GuiObject;
import ch.scaille.example.gui.TestObject;
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
