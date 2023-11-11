package ch.scaille.example.gui;

import ch.scaille.annotations.GuiObject;
import ch.scaille.javabeans.Converters;
import ch.scaille.javabeans.converters.IConverter;

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
		return Converters.listen(o -> o != null && o.aFirstValue != null ? o.aFirstValue : "");
	}
}
