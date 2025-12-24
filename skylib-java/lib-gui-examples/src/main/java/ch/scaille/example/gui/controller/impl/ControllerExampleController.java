package ch.scaille.example.gui.controller.impl;

import ch.scaille.example.gui.TestObject;
import ch.scaille.gui.mvc.GuiController;
import ch.scaille.gui.mvc.GuiModel;
import lombok.Getter;

public class ControllerExampleController extends GuiController {

	@Getter
    private final ControllerExampleModel model = new ControllerExampleModel(GuiModel.of(this));

	public ControllerExampleController() {
		model.getTableModel().insert(new TestObject("World", 2));
		model.getTableModel().insert(new TestObject("Hello", 1));
	}


}
