package ch.skymarshall.tcwriter.gui.editors.params;

import static ch.skymarshall.gui.model.ListModelBindings.values;
import static ch.skymarshall.gui.mvc.factories.Converters.filter;
import static ch.skymarshall.gui.mvc.factories.Converters.listConverter;
import static ch.skymarshall.gui.swing.factories.SwingBindings.group;
import static ch.skymarshall.gui.swing.factories.SwingBindings.selection;
import static ch.skymarshall.gui.swing.factories.SwingBindings.value;
import static ch.skymarshall.gui.swing.factories.SwingBindings.values;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import com.google.common.base.Strings;

import ch.skymarshall.gui.model.ListModel;
import ch.skymarshall.gui.model.views.ListViews;
import ch.skymarshall.gui.mvc.converters.IConverter;
import ch.skymarshall.gui.mvc.factories.BindingDependencies;
import ch.skymarshall.gui.mvc.factories.ObjectTextView;
import ch.skymarshall.gui.mvc.properties.ObjectProperty;
import ch.skymarshall.tcwriter.generators.model.testapi.TestApiParameter;
import ch.skymarshall.tcwriter.generators.model.testapi.TestParameterFactory;
import ch.skymarshall.tcwriter.generators.model.testapi.TestParameterFactory.ParameterNature;
import ch.skymarshall.tcwriter.generators.model.testcase.TestCase;
import ch.skymarshall.tcwriter.generators.model.testcase.TestParameterValue;
import ch.skymarshall.tcwriter.generators.model.testcase.TestReference;
import ch.skymarshall.tcwriter.gui.editors.params.TestParameterValueTableModel.ParameterValueEntry;
import ch.skymarshall.tcwriter.gui.frame.TCWriterController;

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
		return testCase.getTestApi(testParameterValue.getApiParameterId());
	}

	public TestParameterValueEditorPanel(final TCWriterController controller, final TestParameterModel tpModel) {

		final ObjectProperty<TestCase> tc = controller.getModel().getTc();
		final ObjectProperty<TestParameterValue> editedParamValue = tpModel.getEditedParameterValue();

		setLayout(new BorderLayout());

		editedParamValue.bind(v -> !v.equals(TestParameterValue.NO_VALUE)) //
				.listen(this::setVisible);

		editedParamValue
				.bind(v -> !v.getValueFactory().getMandatoryParameters().isEmpty()
						|| !v.getValueFactory().getOptionalParameters().isEmpty()) //
				.listen(this::setEnabled);

		final JPanel topPanel = new JPanel();
		topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.PAGE_AXIS));

		// Simple raw value
		final JRadioButton useRawValue = new JRadioButton("Raw value");
		topPanel.add(useRawValue);

		final JTextField simpleValueEditor = new JTextField();
		tpModel.getSimpleValue().bind(value(simpleValueEditor));
		topPanel.add(simpleValueEditor);

		// Value references
		final JRadioButton useReference = new JRadioButton("Reference: ");
		topPanel.add(useReference);
		final JComboBox<ObjectTextView<TestReference>> referenceEditor = new JComboBox<>();
		tpModel.getReferences()
				.bind(filter(r -> r.getType().equals(editedParamValue.getValue().getValueFactory().getType()))) //
				.bind(listConverter(refToTextConverter())) //
				.bind(values(referenceEditor));
		tpModel.getSelectedReference().bind(refToTextConverter())//
				.bind(selection(referenceEditor)) //
				.addDependency(BindingDependencies.preserveOnUpdateOf(tpModel.getReferences()));
		topPanel.add(referenceEditor);

		// Complex type
		final JRadioButton useComplexType = new JRadioButton("Test Api: ");
		topPanel.add(useComplexType);
		add(topPanel, BorderLayout.NORTH);

		final ListModel<ParameterValueEntry> allEditedParameters = new ListModel<>(
				ListViews.<ParameterValueEntry>sorted());
		final ListModel<ParameterValueEntry> visibleParameters = allEditedParameters
				.child(ListViews.filtered(p -> p.visible));
		final TestParameterValueTable valueTable = new TestParameterValueTable(
				new TestParameterValueTableModel(visibleParameters));
		valueTable.setName(tpModel.getPrefix() + "-valueTable");
		final ObjectProperty<Map<String, TestParameterValue>> complexValues = editedParamValue.child("ComplexParams",
				TestParameterValue::getComplexTypeValues, TestParameterValue::updateComplexTypeValues);
		complexValues.bind(toListModel(tc, editedParamValue)).bind(values(allEditedParameters));

		tpModel.getTestApi().listen(api -> fixParamsOfApi(tc, api, editedParamValue, allEditedParameters));

		add(new JScrollPane(valueTable), BorderLayout.CENTER);

		final ButtonGroup group = new ButtonGroup();
		group.add(useRawValue);
		group.add(useReference);
		group.add(useComplexType);

		tpModel.getValueNature().bind(group(group, ParameterNature.REFERENCE, useReference, ParameterNature.SIMPLE_TYPE,
				useRawValue, ParameterNature.TEST_API, useComplexType));

		// When TC or parameters are changing, update the list of references
		tc.listen(test -> tpModel.getReferences().setValue(this, getReferences(test, editedParamValue.getValue())));
		editedParamValue.listen(values -> tpModel.getReferences().setValue(this, getReferences(tc.getValue(), values)));

		// when updating the parameter value, update the reference
		editedParamValue.listen(value -> {
			tpModel.getValueNature().setValue(this, value.getValueFactory().getNature());
			if (value.getValueFactory().getNature() == ParameterNature.REFERENCE) {
				tpModel.getSelectedReference().setValue(this, (TestReference) value.getValueFactory());
			} else {
				tpModel.getSelectedReference().setValue(this, null);
			}
		});

		// Edit the parameter value when changing the reference.
		tpModel.getSelectedReference().listen(ref -> {
			if (tpModel.getEditedParameterValue().getObjectValue().getValueFactory()
					.getNature() == ParameterNature.REFERENCE) {
				editedParamValue.getValue().setValueFactory(ref);
			}
		});

		// Edit the parameter value when changing the nature of the factory.
		tpModel.getValueNature().listen(v -> {
			final TestParameterValue paramValue = editedParamValue.getValue();

			switch (v) {
			case SIMPLE_TYPE:
				paramValue.setValueFactory(apiOf(tc.getValue(), paramValue).asSimpleParameter());
				break;
			case REFERENCE:
				final TestReference ref = tpModel.getSelectedReference().getValue();
				if (ref != null) {
					paramValue.setValueFactory(ref);
				}
				break;
			case TEST_API:
				paramValue.setValueFactory(tpModel.getTestApi().getValue());
				break;
			default:
				break;
			}
		});
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
		final Set<String> missingMandatoryIds = api.getMandatoryParameters().stream().map(TestApiParameter::getId)
				.collect(Collectors.toSet());
		final Set<String> missingOptionalIds = api.getOptionalParameters().stream().map(TestApiParameter::getId)
				.collect(Collectors.toSet());
		for (final ParameterValueEntry p : allEditedParameters) {
			missingMandatoryIds.remove(p.id);
			missingOptionalIds.remove(p.id);
			allEditedParameters.editValue(p, e -> {
				e.visible = false;
				updateParam(e, api);
			});
		}
		// Add missing values in parameter value and in table
		final List<ParameterValueEntry> newValues = new ArrayList<>();
		for (final String mandatoryId : missingMandatoryIds) {
			final TestParameterValue value = new TestParameterValue(mandatoryId,
					tc.getValue().descriptionOf(mandatoryId).getDescription(),
					api.getMandatoryParameterById(mandatoryId).asSimpleParameter(), null);
			editedParamValue.getValue().addComplexTypeValue(value);
			newValues.add(asParam(tc.getObjectValue(), mandatoryId, value, api));
		}
		for (final String optionalId : missingOptionalIds) {
			final TestParameterValue value = new TestParameterValue(optionalId,
					tc.getValue().descriptionOf(optionalId).getDescription(),
					api.getOptionalParameterById(optionalId).asSimpleParameter(), null);
			editedParamValue.getValue().addComplexTypeValue(value);
			newValues.add(asParam(tc.getObjectValue(), optionalId, value, api));
		}
		allEditedParameters.addValues(newValues);
	}

	public static final IConverter<Map<String, TestParameterValue>, Collection<ParameterValueEntry>> toListModel(
			final ObjectProperty<TestCase> tc, final ObjectProperty<TestParameterValue> propertyValue) {

		return new IConverter<Map<String, TestParameterValue>, Collection<ParameterValueEntry>>() {

			@Override
			public List<ParameterValueEntry> convertPropertyValueToComponentValue(
					final Map<String, TestParameterValue> values) {
				final List<ParameterValueEntry> paramList = new ArrayList<>();
				for (final Entry<String, TestParameterValue> value : values.entrySet()) {
					paramList.add(asParam(tc.getValue(), value.getKey(), value.getValue(),
							propertyValue.getValue().getValueFactory()));
				}
				return paramList;
			}

			@Override
			public Map<String, TestParameterValue> convertComponentValueToPropertyValue(
					final Collection<ParameterValueEntry> componentValue) {
				final Map<String, TestParameterValue> result = new HashMap<>();
				for (final ParameterValueEntry pv : componentValue) {
					if (!pv.enabled && !pv.mandatory) {
						continue;
					}
					result.put(pv.id, new TestParameterValue(pv.id, pv.factory, pv.value));
				}
				return result;
			}

		};

	}

	private static void updateParam(final ParameterValueEntry paramValue,
			final TestParameterFactory complexTypeFactory) {
		final boolean mandatory = complexTypeFactory.hasMandatoryParameter(paramValue.id);
		final boolean optional = complexTypeFactory.hasOptionalParameter(paramValue.id);
		paramValue.update(mandatory, mandatory || optional);
	}

	private static ParameterValueEntry asParam(final TestCase tc, final String complexParameterId,
			final TestParameterValue complexValue, final TestParameterFactory complexTypeFactory) {
		final String simpleValue = complexValue.getSimpleValue();
		final ParameterValueEntry paramValue = new ParameterValueEntry(complexParameterId,
				complexValue.getValueFactory(), tc.descriptionOf(complexParameterId).getDescription(), simpleValue,
				!Strings.isNullOrEmpty(simpleValue));
		updateParam(paramValue, complexTypeFactory);
		return paramValue;
	}

}
