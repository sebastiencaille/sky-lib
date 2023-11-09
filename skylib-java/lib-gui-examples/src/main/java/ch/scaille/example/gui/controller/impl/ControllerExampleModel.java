package ch.scaille.example.gui.controller.impl;

import java.util.Comparator;

import ch.scaille.example.gui.TestObject;
import ch.scaille.gui.model.ListModel;
import ch.scaille.gui.model.views.ListViews;
import ch.scaille.gui.validation.ValidationBinding;
import ch.scaille.javabeans.properties.ErrorSet;
import ch.scaille.javabeans.properties.ObjectProperty;

public class ControllerExampleModel extends ControllerExampleObjectGuiModel {

	private static final Comparator<TestObject> TEST_COMPARATOR = Comparator.comparing(TestObject::getAFirstValue);

	private final ObjectProperty<String> staticListSelection = new ObjectProperty<>("StaticListSelection", this);

	private final ObjectProperty<String> dynamicListObjectProperty = new ObjectProperty<>("DynamicListObjectProperty",
			this);

	private final ObjectProperty<TestObject> complexProperty = new ObjectProperty<>("ComplexObject", this);

	private final ListModel<TestObject> tableModel = new ListModel<>(ListViews.sorted(TEST_COMPARATOR));

	public ControllerExampleModel(final ModelConfiguration config) {
		super(config.with(ValidationBinding.validator()).with(new ErrorSet("Errors", config.getPropertySupport())));
	}

	public ObjectProperty<String> getStaticListSelectionProperty() {
		return staticListSelection;
	}

	public ObjectProperty<String> getDynamicListObjectProperty() {
		return dynamicListObjectProperty;
	}

	public ObjectProperty<TestObject> getComplexProperty() {
		return complexProperty;
	}

	public ListModel<TestObject> getTableModel() {
		return tableModel;
	}

}
