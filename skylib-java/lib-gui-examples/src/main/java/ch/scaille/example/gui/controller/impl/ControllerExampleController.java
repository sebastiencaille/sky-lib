package ch.scaille.example.gui.controller.impl;

import ch.scaille.example.gui.TestObject;
import ch.scaille.gui.mvc.GuiController;
import ch.scaille.gui.mvc.GuiModel;
import ch.scaille.javabeans.PropertyGroup;

public class ControllerExampleController extends GuiController {

	private final ControllerExampleModel model = new ControllerExampleModel(GuiModel.of(this));
	private final PropertyGroup modelPropertiesGroup = new PropertyGroup();

	public ControllerExampleController() {
		modelPropertiesGroup.addProperty(model.getStaticListSelectionProperty());
		model.getTableModel().insert(new TestObject("World", 2));
		model.getTableModel().insert(new TestObject("Hello", 1));
	}

	public ControllerExampleModel getModel() {
		return model;
	}

	public PropertyGroup getDynamicListUpdater() {
		return modelPropertiesGroup;
	}

}
