package ch.scaille.tcwriter.gui.editors.params;

import ch.scaille.gui.mvc.GuiModel;
import ch.scaille.gui.mvc.properties.ListProperty;
import ch.scaille.gui.mvc.properties.ObjectProperty;
import ch.scaille.tcwriter.gui.frame.TCWriterController;
import ch.scaille.tcwriter.model.testapi.TestParameterFactory;
import ch.scaille.tcwriter.model.testcase.TestParameterValue;
import ch.scaille.tcwriter.model.testcase.TestReference;

public class TestParameterModel extends GuiModel {
	private final ObjectProperty<TestParameterFactory.ParameterNature> valueNature;
	private final ObjectProperty<String> simpleValue;
	private final ObjectProperty<TestReference> selectedReference;
	private final ListProperty<TestReference> references;
	private final ObjectProperty<TestParameterFactory> testApi;
	private final ObjectProperty<TestParameterValue> editedParameterValue;
	private final String prefix;

	public TestParameterModel(final String prefix, final TCWriterController guiController,
			final ObjectProperty<TestParameterFactory> testApi,
			final ObjectProperty<TestParameterValue> editedParameterValue) {
		super(with(guiController.getScopedChangeSupport().getMain().scoped(prefix + "-controller")));
		this.prefix = prefix;
		this.editedParameterValue = editedParameterValue;
		this.testApi = testApi;

		valueNature = new ObjectProperty<>(prefix + "-nature", this);
		simpleValue = editedParameterValue.child(prefix + "-simpleValue", TestParameterValue::getSimpleValue,
				TestParameterValue::setSimpleValue);
		selectedReference = new ObjectProperty<>(prefix + "-reference", this);
		references = new ListProperty<>(prefix + "-references", this);
		editedParameterValue.addListener(getPropertySupport().detachWhenPropLoading());
	}

	public String getPrefix() {
		return prefix;
	}

	public ObjectProperty<TestParameterFactory.ParameterNature> getValueNature() {
		return valueNature;
	}

	public ObjectProperty<String> getSimpleValue() {
		return simpleValue;
	}

	public ListProperty<TestReference> getReferences() {
		return references;
	}

	public ObjectProperty<TestReference> getSelectedReference() {
		return selectedReference;
	}

	public ObjectProperty<TestParameterFactory> getTestApi() {
		return testApi;
	}

	public ObjectProperty<TestParameterValue> getEditedParameterValue() {
		return editedParameterValue;
	}

}
