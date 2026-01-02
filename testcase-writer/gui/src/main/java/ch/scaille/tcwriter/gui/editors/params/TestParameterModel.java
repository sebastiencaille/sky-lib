package ch.scaille.tcwriter.gui.editors.params;

import ch.scaille.gui.mvc.GuiModel;
import ch.scaille.javabeans.properties.ListProperty;
import ch.scaille.javabeans.properties.ObjectProperty;
import ch.scaille.tcwriter.gui.frame.TCWriterController;
import ch.scaille.tcwriter.model.dictionary.ParameterNature;
import ch.scaille.tcwriter.model.dictionary.TestParameterFactory;
import ch.scaille.tcwriter.model.testcase.TestParameterValue;
import ch.scaille.tcwriter.model.testcase.TestReference;
import lombok.Getter;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@Getter
@NullMarked
public class TestParameterModel extends GuiModel {
	private final ObjectProperty<ParameterNature> valueNature;
	private final ObjectProperty<@Nullable String> simpleValue;
	private final ObjectProperty<@Nullable TestReference> selectedReference;
	private final ListProperty<TestReference> references;
	private final ObjectProperty<@Nullable TestParameterFactory> testApi;
	private final ObjectProperty<TestParameterValue> editedParameterValue;
	private final String prefix;

	public TestParameterModel(final String prefix, final TCWriterController guiController,
			final ObjectProperty<TestParameterFactory> testApi,
			final ObjectProperty<TestParameterValue> editedParameterValue) {
		super(ModelConfiguration.builder().propertySupport(guiController.getScopedChangeSupport().getChangeSupport().scoped(prefix + "-controller")));
		this.prefix = prefix;
		this.editedParameterValue = editedParameterValue;
		this.testApi = testApi;

		this.valueNature = new ObjectProperty<>(prefix + "-nature", this, ParameterNature.SIMPLE_TYPE);
		this.simpleValue = editedParameterValue.child(prefix + "-simpleValue", TestParameterValue::getSimpleValue,
				TestParameterValue::setSimpleValue);
		this.selectedReference = new ObjectProperty<>(prefix + "-reference", this, null);
		this.references = new ListProperty<>(prefix + "-references", this);
		this.editedParameterValue.addListener(getPropertySupport().detachWhenPropLoading());
	}

}
