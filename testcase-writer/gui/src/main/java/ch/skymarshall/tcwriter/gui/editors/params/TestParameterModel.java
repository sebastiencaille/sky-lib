package ch.skymarshall.tcwriter.gui.editors.params;

import ch.skymarshall.gui.mvc.GuiModel;
import ch.skymarshall.gui.mvc.IScopedSupport;
import ch.skymarshall.gui.mvc.properties.ListProperty;
import ch.skymarshall.gui.mvc.properties.ObjectProperty;
import ch.skymarshall.tcwriter.generators.model.testapi.TestParameterFactory;
import ch.skymarshall.tcwriter.generators.model.testcase.TestParameterValue;
import ch.skymarshall.tcwriter.generators.model.testcase.TestReference;
import ch.skymarshall.tcwriter.gui.frame.TCWriterController;

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
		super(guiController.getScopedChangeSupport().getMain().scoped(prefix + "-controller"));
		this.prefix = prefix;
		this.editedParameterValue = editedParameterValue;
		this.testApi = testApi;

		valueNature = new ObjectProperty<>(prefix + "-nature", propertySupport); 
		simpleValue = editedParameterValue.child(prefix + "-simpleValue", TestParameterValue::getSimpleValue,
				TestParameterValue::setSimpleValue);
		selectedReference = new ObjectProperty<>(prefix + "-reference", propertySupport);

		references = new ListProperty<>(prefix + "-references", propertySupport);

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
