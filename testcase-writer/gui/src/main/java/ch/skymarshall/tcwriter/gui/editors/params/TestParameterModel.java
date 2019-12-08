package ch.skymarshall.tcwriter.gui.editors.params;

import ch.skymarshall.gui.mvc.IScopedSupport;
import ch.skymarshall.gui.mvc.properties.ListProperty;
import ch.skymarshall.gui.mvc.properties.ObjectProperty;
import ch.skymarshall.tcwriter.generators.model.testapi.TestParameterFactory;
import ch.skymarshall.tcwriter.generators.model.testcase.TestParameterValue;
import ch.skymarshall.tcwriter.generators.model.testcase.TestReference;
import ch.skymarshall.tcwriter.gui.frame.TCWriterController;

public class TestParameterModel {
	private final ObjectProperty<TestParameterFactory.ParameterNature> valueNature;
	private final ObjectProperty<String> simpleValue;
	private final ObjectProperty<TestReference> selectedReference;
	private final ListProperty<TestReference> references;
	private final ObjectProperty<TestParameterFactory> testApi;
	private final ObjectProperty<TestParameterValue> editedParameterValue;

	public TestParameterModel(final String prefix, final TCWriterController guiController,
			final ObjectProperty<TestParameterValue> editedParameterValue,
			final ObjectProperty<TestParameterFactory> testApi) {
		this.editedParameterValue = editedParameterValue;
		this.testApi = testApi;
		final IScopedSupport propertyChangeSupport = guiController.getChangeSupport();
		valueNature = new ObjectProperty<>(prefix + "-nature", propertyChangeSupport);
		simpleValue = editedParameterValue.child(prefix + "-simpleValue", TestParameterValue::getSimpleValue,
				TestParameterValue::setSimpleValue);
		selectedReference = new ObjectProperty<>(prefix + "-reference", propertyChangeSupport);

		references = new ListProperty<>(prefix + "-references", propertyChangeSupport);
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
