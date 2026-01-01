package ch.scaille.example.gui.controller.impl;

import java.util.Comparator;
import java.util.List;

import ch.scaille.example.gui.TestObject;
import ch.scaille.gui.model.ListModel;
import ch.scaille.gui.model.views.ListViews;
import ch.scaille.gui.validation.ValidationBinding;
import ch.scaille.javabeans.properties.ObjectProperty;
import lombok.Getter;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
@Getter
public class ControllerExampleModel extends ControllerExampleObjectGuiModel {

	private static final Comparator<TestObject> TEST_COMPARATOR = Comparator.comparing(TestObject::getAFirstValue);

	private final ObjectProperty<@Nullable String> staticListSelectionProperty = new ObjectProperty<>("StaticListSelection", this, null);

	private final ObjectProperty<@Nullable String> dynamicListObjectProperty = new ObjectProperty<>("DynamicListObjectProperty",
			this, null);

	private final ObjectProperty<@Nullable TestObject> complexProperty = new ObjectProperty<>("ComplexObject", this, null);

	private final ListModel<TestObject> tableModel = new ListModel<>(ListViews.sorted(TEST_COMPARATOR));

	public ControllerExampleModel(final ModelConfiguration.ModelConfigurationBuilder config) {
		super("", config.implicitConverters(List.of(ValidationBinding.validator())));
	}

}
