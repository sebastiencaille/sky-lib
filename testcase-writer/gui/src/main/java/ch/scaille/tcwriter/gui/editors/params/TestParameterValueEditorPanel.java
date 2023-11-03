package ch.scaille.tcwriter.gui.editors.params;

import static ch.scaille.gui.model.ListModelBindings.values;
import static ch.scaille.gui.swing.factories.SwingBindings.group;
import static ch.scaille.gui.swing.factories.SwingBindings.selection;
import static ch.scaille.gui.swing.factories.SwingBindings.value;
import static ch.scaille.gui.swing.factories.SwingBindings.values;
import static ch.scaille.javabeans.Converters.filter;
import static ch.scaille.javabeans.Converters.listConverter;
import static java.util.stream.Collectors.toSet;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import com.google.common.base.Strings;

import ch.scaille.gui.model.ListModel;
import ch.scaille.gui.model.views.ListViews;
import ch.scaille.gui.mvc.factories.ObjectTextView;
import ch.scaille.javabeans.BindingDependencies;
import ch.scaille.javabeans.converters.IConverter;
import ch.scaille.javabeans.properties.ObjectProperty;
import ch.scaille.tcwriter.gui.editors.params.TestParameterValueTableModel.ParameterValueEntry;
import ch.scaille.tcwriter.gui.frame.TCWriterController;
import ch.scaille.tcwriter.model.dictionary.TestApiParameter;
import ch.scaille.tcwriter.model.dictionary.TestParameterFactory;
import ch.scaille.tcwriter.model.dictionary.TestParameterFactory.ParameterNature;
import ch.scaille.tcwriter.model.testcase.ExportableTestParameterValue;
import ch.scaille.tcwriter.model.testcase.TestCase;
import ch.scaille.tcwriter.model.testcase.TestParameterValue;
import ch.scaille.tcwriter.model.testcase.TestReference;

public class TestParameterValueEditorPanel extends JPanel {

	public static IConverter<TestReference, ObjectTextView<TestReference>> refToTextConverter() {
		return ObjectTextView.converter(ref -> ref.toDescription().getDescription());
	}

	private List<TestReference> getReferences(final TestCase testCase, final TestParameterValue testParameterValue) {
		if (testParameterValue.getApiParameterId().isEmpty()) {
			return Collections.emptyList();
		}
		return new ArrayList<>(testCase.getSuitableReferences(apiOf(testCase, testParameterValue)));
	}

	private TestApiParameter apiOf(final TestCase testCase, final TestParameterValue testParameterValue) {
		return testCase.getTestParameter(testParameterValue.getApiParameterId());
	}

	public TestParameterValueEditorPanel(final TCWriterController controller, final TestParameterModel tpModel) {

		final var testCase = controller.getModel().getTestCase();
		final var editedParamValue = tpModel.getEditedParameterValue();

		setLayout(new BorderLayout());

		editedParamValue.bind(v -> !v.equals(ExportableTestParameterValue.NO_VALUE)) //
				.listen(this::setVisible);

		editedParamValue
				.bind(v -> !v.getValueFactory().getMandatoryParameters().isEmpty()
						|| !v.getValueFactory().getOptionalParameters().isEmpty()) //
				.listen(this::setEnabled);

		final var topPanel = new JPanel();
		topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.PAGE_AXIS));

		final var valueTypePanel = new JPanel();
		
		// To select the kind of value 

		final var useRawValueEditor = new JRadioButton("Raw value");
		tpModel.getTestApi().bind(o -> o != null && o.getNature() != ParameterNature.TEST_API).listen(useRawValueEditor::setEnabled);
		valueTypePanel.add(useRawValueEditor);

		final var useReferenceEditor = new JRadioButton("Reference: ");
		valueTypePanel.add(useReferenceEditor);
		
		final var useComplexTypeEditor = new JRadioButton("Test Api: ");
		tpModel.getTestApi().bind(o -> o != null && o.getNature() == ParameterNature.TEST_API).listen(useComplexTypeEditor::setEnabled);
		valueTypePanel.add(useComplexTypeEditor);
		
		topPanel.add(valueTypePanel);
		
		final var paramTypeEditorGroup = new ButtonGroup();
		paramTypeEditorGroup.add(useRawValueEditor);
		paramTypeEditorGroup.add(useReferenceEditor);
		paramTypeEditorGroup.add(useComplexTypeEditor);
		
		// editors 
		
		final var simpleValueEditor = new JTextField();
		tpModel.getSimpleValue().bind(value(simpleValueEditor));
		tpModel.getValueNature().bind(v -> v == ParameterNature.SIMPLE_TYPE).listen(enableAndVisible(simpleValueEditor));
		topPanel.add(simpleValueEditor);
		
		
		final var referenceEditor = new JComboBox<ObjectTextView<TestReference>>();
		tpModel.getReferences()
				.bind(filter(r -> r.getParameterType()
						.equals(editedParamValue.getValue().getValueFactory().getParameterType()))) //
				.bind(listConverter(refToTextConverter())) //
				.bind(values(referenceEditor));
		tpModel.getSelectedReference().bind(refToTextConverter())//
				.bind(selection(referenceEditor)) //
				.addDependency(BindingDependencies.preserveOnUpdateOf(tpModel.getReferences()));
		tpModel.getValueNature().bind(v -> v == ParameterNature.REFERENCE).listen(enableAndVisible(referenceEditor));
		topPanel.add(referenceEditor);

		// Complex type editor
		add(topPanel, BorderLayout.NORTH);
 
		final var allEditedParameters = new ListModel<ParameterValueEntry>(ListViews.sorted());
		final var visibleParameters = allEditedParameters.child(ListViews.filtered(p -> p.visible));
		final var valueTable = new TestParameterValueTable(new TestParameterValueTableModel(visibleParameters));
		valueTable.setName(tpModel.getPrefix() + "-valueTable");
		final var complexValues = editedParamValue.child("ComplexParams", TestParameterValue::getComplexTypeValues,
				TestParameterValue::updateComplexTypeValues);
		complexValues.bind(toListModel(testCase, editedParamValue)).bind(values(allEditedParameters));
		var  valueTablePane = new JScrollPane(valueTable);
		tpModel.getValueNature().bind(v -> v == ParameterNature.TEST_API).listen(enableAndVisible(valueTablePane));
		tpModel.getTestApi().listenActive(api -> fixParamsOfApi(testCase, api, editedParamValue, allEditedParameters));

		add(valueTablePane, BorderLayout.CENTER);

		tpModel.getValueNature().bind(group(paramTypeEditorGroup, ParameterNature.REFERENCE, useReferenceEditor,
				ParameterNature.SIMPLE_TYPE, useRawValueEditor, ParameterNature.TEST_API, useComplexTypeEditor));

		// When TC or parameters are changing, update the list of references
		testCase.listenActive(
				test -> tpModel.getReferences().setValue(this, getReferences(test, editedParamValue.getValue())));
		editedParamValue.listenActive(
				values -> tpModel.getReferences().setValue(this, getReferences(testCase.getValue(), values)));

		// when updating the parameter value, update the reference
		editedParamValue.listenActive(value -> {
			tpModel.getValueNature().setValue(this, value.getValueFactory().getNature());
			if (value.getValueFactory().getNature() == ParameterNature.REFERENCE) {
				tpModel.getSelectedReference().setValue(this, (TestReference) value.getValueFactory());
			} else {
				tpModel.getSelectedReference().setValue(this, null);
			}
		});

		// Edit the parameter value when changing the reference.
		tpModel.getSelectedReference().listenActive(ref -> {
			if (tpModel.getEditedParameterValue().getObjectValue().getValueFactory()
					.getNature() == ParameterNature.REFERENCE) {
				editedParamValue.getValue().setParameterFactory(ref);
			}
		});

		// Edit the parameter value when changing the nature of the factory.
		tpModel.getValueNature().listenActive(v -> {
			final var paramValue = editedParamValue.getValue();

			switch (v) {
			case SIMPLE_TYPE:
				paramValue.setParameterFactory(apiOf(testCase.getValue(), paramValue).asSimpleParameter());
				break;
			case REFERENCE:
				final var testRef = tpModel.getSelectedReference().getValue();
				if (testRef != null) {
					paramValue.setParameterFactory(testRef);
				}
				break;
			case TEST_API:
				paramValue.setParameterFactory(tpModel.getTestApi().getValue());
				break;
			default:
				break;
			}
		});
	}

	private Consumer<Boolean> enableAndVisible(JComponent component) {
		return e -> {
			component.setEnabled(e);
			component.setVisible(e);
			component.revalidate();
		};
	}

	/**
	 * Update the values table when changing the api. Don't trigger when loading the
	 * step, because we may not have the right value during the loading
	 */
	private void fixParamsOfApi(final ObjectProperty<TestCase> tc, final TestParameterFactory api,
			final ObjectProperty<TestParameterValue> editedParamValue,
			final ListModel<ParameterValueEntry> allEditedParameters) {
		if (api == null) {
			return;
		}
		// Update existing values, track missing ones
		final var missingMandatoryIds = api.getMandatoryParameters().stream().map(TestApiParameter::getId)
				.collect(toSet());
		final var missingOptionalIds = api.getOptionalParameters().stream().map(TestApiParameter::getId)
				.collect(toSet());
		for (final var editorParamValue : allEditedParameters) {
			missingMandatoryIds.remove(editorParamValue.id);
			missingOptionalIds.remove(editorParamValue.id);
			allEditedParameters.editValue(editorParamValue, e -> {
				e.visible = false;
				updateParam(e, api);
			});
		}
		// Add missing values in parameter value and in table
		final var newValues = new ArrayList<ParameterValueEntry>();
		for (final var mandatoryId : missingMandatoryIds) {
			final var newValue = new ExportableTestParameterValue(mandatoryId,
					tc.getValue().descriptionOf(mandatoryId).getDescription(),
					api.getMandatoryParameterById(mandatoryId).asSimpleParameter(), null);
			editedParamValue.getValue().addComplexTypeValue(newValue);
			newValues.add(asParam(tc.getObjectValue(), mandatoryId, newValue, api));
		}
		for (final var optionalId : missingOptionalIds) {
			final var newValue = new ExportableTestParameterValue(optionalId,
					tc.getValue().descriptionOf(optionalId).getDescription(),
					api.getOptionalParameterById(optionalId).asSimpleParameter(), null);
			editedParamValue.getValue().addComplexTypeValue(newValue);
			newValues.add(asParam(tc.getObjectValue(), optionalId, newValue, api));
		}
		allEditedParameters.addValues(newValues);
	}

	public static IConverter<Map<String, TestParameterValue>, Collection<ParameterValueEntry>> toListModel(
			final ObjectProperty<TestCase> tc, final ObjectProperty<TestParameterValue> propertyValue) {

		return new IConverter<>() {

			@Override
			public List<ParameterValueEntry> convertPropertyValueToComponentValue(
					final Map<String, TestParameterValue> values) {
				final var paramList = new ArrayList<ParameterValueEntry>();
				for (final var paramValue : values.entrySet()) {
					paramList.add(asParam(tc.getValue(), paramValue.getKey(), paramValue.getValue(),
							propertyValue.getValue().getValueFactory()));
				}
				return paramList;
			}

			@Override
			public Map<String, TestParameterValue> convertComponentValueToPropertyValue(
					final Collection<ParameterValueEntry> componentValue) {
				final var result = new HashMap<String, TestParameterValue>();
				for (final var parameterValueEntry : componentValue) {
					if (!parameterValueEntry.enabled && !parameterValueEntry.mandatory) {
						continue;
					}
					result.put(parameterValueEntry.id, new ExportableTestParameterValue(parameterValueEntry.id,
							parameterValueEntry.factory, parameterValueEntry.value));
				}
				return result;
			}

		};

	}

	private static void updateParam(final ParameterValueEntry paramValue,
			final TestParameterFactory complexTypeFactory) {
		final var mandatory = complexTypeFactory.hasMandatoryParameter(paramValue.id);
		final var optional = complexTypeFactory.hasOptionalParameter(paramValue.id);
		paramValue.update(mandatory, mandatory || optional);
	}

	private static ParameterValueEntry asParam(final TestCase tc, final String complexParameterId,
			final TestParameterValue complexValue, final TestParameterFactory complexTypeFactory) {
		final var simpleValue = complexValue.getSimpleValue();
		final var newParamValue = new ParameterValueEntry(complexParameterId, complexValue.getValueFactory(),
				tc.descriptionOf(complexParameterId).getDescription(), simpleValue,
				!Strings.isNullOrEmpty(simpleValue));
		updateParam(newParamValue, complexTypeFactory);
		return newParamValue;
	}

}
