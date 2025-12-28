package ch.scaille.example.gui.controller.impl;

import ch.scaille.annotations.GuiObject;
import ch.scaille.example.gui.TestObject;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@GuiObject
public class ControllerExampleObject {

	private boolean booleanProp;
	private int intProp;
	@NotBlank
	private String stringProp;
	private TestObject testObjectProp;

}
