package ch.scaille.example.gui;

import ch.scaille.annotations.GuiObject;
import ch.scaille.javabeans.converters.Converters;
import ch.scaille.javabeans.converters.IConverter;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@GuiObject
public class TestObject {

	public TestObject(final String afirstValue, final int i) {
		this.aFirstValue = afirstValue;
		aSecondValue = i;
	}

	private String aFirstValue;

	private int aSecondValue;

	public static IConverter<TestObject, String> testObjectToString() {
		return Converters.listen(o -> o != null && o.aFirstValue != null ? o.aFirstValue : "");
	}
}
