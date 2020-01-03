package ch.skymarshall.tcwriter.gui.editors.params;

import static ch.skymarshall.gui.model.ListModelBindings.values;
import static ch.skymarshall.gui.mvc.BindingDependencies.detachOnUpdateOf;
import static ch.skymarshall.gui.mvc.converters.Converters.filter;
import static ch.skymarshall.gui.mvc.converters.Converters.listConverter;
import static ch.skymarshall.gui.swing.bindings.SwingBindings.group;
import static ch.skymarshall.gui.swing.bindings.SwingBindings.selection;
import static ch.skymarshall.gui.swing.bindings.SwingBindings.value;
import static ch.skymarshall.gui.swing.bindings.SwingBindings.values;

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

import ch.skymarshall.gui.model.ChildListModel;
import ch.skymarshall.gui.model.ListModel;
import ch.skymarshall.gui.model.RootListModel;
import ch.skymarshall.gui.model.views.ListViews;
import ch.skymarshall.gui.mvc.converters.IConverter;
import ch.skymarshall.gui.mvc.properties.ObjectProperty;
import ch.skymarshall.gui.swing.bindings.ObjectTextView;
import ch.skymarshall.tcwriter.generators.model.testapi.TestApiParameter;
import ch.skymarshall.tcwriter.generators.model.testapi.TestParameterFactory;
import ch.skymarshall.tcwriter.generators.model.testapi.TestParameterFactory.ParameterNature;
import ch.skymarshall.tcwriter.generators.model.testcase.TestCase;
import ch.skymarshall.tcwriter.generators.model.testcase.TestParameterValue;
import ch.skymarshall.tcwriter.generators.model.testcase.TestReference;
import ch.skymarshall.tcwriter.gui.editors.params.TestParameterValueTableModel.ParameterValue;
import ch.skymarshall.tcwriter.gui.frame.TCWriterController;
import ch.skymarshall.tcwriter.gui.frame.TCWriterModel;

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

	public TestParameterValueEditorPanel(final TCWriterController controller, final TCWriterModel tcWriterModel,
			final TestParameterModel tpModel) {
		final ObjectProperty<TestCase> tc = controller.getModel().getTc();
		final ObjectProperty<TestParameterValue> editedParamValue = tpModel.getEditedParameterValue();

		tpModel.getEditedParameterValue().bind(v -> !v.equals(TestParameterValue.NO_VALUE)) //
				.listen(this::setVisible);

		tc.listen(test -> tpModel.getReferences().setValue(this, getReferences(test, editedParamValue.getValue())));
		editedParamValue.listen(values -> tpModel.getReferences().setValue(this, getReferences(tc.getValue(), values)));

		setLayout(new BorderLayout());

		final JPanel topPanel = new JPanel();
		topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.PAGE_AXIS));

		final JRadioButton useRawValue = new JRadioButton("Raw value");
		topPanel.add(useRawValue);

		final JTextField simpleValueEditor = new JTextField();
		tpModel.getSimpleValue().bind(value(simpleValueEditor));
		topPanel.add(simpleValueEditor);

		final JRadioButton useReference = new JRadioButton("Reference: ");
		topPanel.add(useReference);
		final JComboBox<ObjectTextView<TestReference>> referenceEditor = new JComboBox<>();
		tpModel.getReferences()
				.bind(filter(r -> r.getType().equals(editedParamValue.getValue().getValueFactory().getType()))) //
				.bind(listConverter(refToTextConverter())) //
				.bind(values(referenceEditor));
		tpModel.getSelectedReference().bind(refToTextConverter())//
				.bind(selection(referenceEditor)) //
				.addDependency(detachOnUpdateOf(tpModel.getReferences()));
		topPanel.add(referenceEditor);

		final JRadioButton useComplexType = new JRadioButton("Test Api: ");
		topPanel.add(useComplexType);
		add(topPanel, BorderLayout.NORTH);

		// List of mandatory / optional parameters
		final ListModel<ParameterValue> allEditedParameters = new RootListModel<>(ListViews.<ParameterValue>sorted());
		final ListModel<ParameterValue> visibleParameters = new ChildListModel<>(allEditedParameters,
				ListViews.filtered(p -> p.visible));
		final TestParameterValueTable view = new TestParameterValueTable(
				new TestParameterValueTableModel(visibleParameters));
		final ObjectProperty<Map<String, TestParameterValue>> complexValues = editedParamValue.child("ComplexParams",
				TestParameterValue::getComplexTypeValues, TestParameterValue::updateComplexTypeValues);
		complexValues.bind(toListModel(tc.getValue(), editedParamValue)).bind(values(allEditedParameters));
		tpModel.getTestApi().listen(api -> {
			if (api == null) {
				return;
			}
			final Set<String> missingMandatoryNames = api.getMandatoryParameters().stream().map(TestApiParameter::getName)
					.collect(Collectors.toSet());
			final Set<String> missingOptionalNames = api.getOptionalParameters().stream().map(TestApiParameter::getName)
					.collect(Collectors.toSet());
			for (final ParameterValue p : allEditedParameters) {
				missingMandatoryNames.remove(p.name);
				missingOptionalNames.remove(p.name);
				allEditedParameters.editValue(p, e -> {
					e.visible = false;
					updateParam(e, api);
				});
			}
			final List<ParameterValue> newValues = new ArrayList<>();
			for (final String name : missingMandatoryNames) {
				final TestParameterValue value = new TestParameterValue(name, name,
						api.getMandatoryParameter(name).asSimpleParameter(), null);
				editedParamValue.getValue().addComplexTypeValue(value);
				newValues.add(asParam(tc.getObjectValue(), name, value, api));
			}
			for (final String name : missingOptionalNames) {
				final TestParameterValue value = new TestParameterValue(name, name,
						api.getOptionalParameter(name).asSimpleParameter(), null);
				editedParamValue.getValue().addComplexTypeValue(value);
				newValues.add(asParam(tc.getObjectValue(), name, value, api));
			}
			allEditedParameters.addValues(newValues);
		});

		add(new JScrollPane(view), BorderLayout.CENTER);

		final ButtonGroup group = new ButtonGroup();
		group.add(useRawValue);
		group.add(useReference);
		group.add(useComplexType);

		tpModel.getValueNature().bind(group(group, ParameterNature.REFERENCE, useReference, ParameterNature.SIMPLE_TYPE,
				useRawValue, ParameterNature.TEST_API, useComplexType));

		editedParamValue.listen(value -> {
			tpModel.getValueNature().setValue(this, value.getValueFactory().getNature());
			if (value.getValueFactory().getNature() == ParameterNature.REFERENCE) {
				tpModel.getSelectedReference().setValue(this, (TestReference) value.getValueFactory());
			} else {
				tpModel.getSelectedReference().setValue(this, null);
			}
		});

		tpModel.getSelectedReference().listen(ref -> {
			if (tpModel.getEditedParameterValue().getObjectValue().getValueFactory()
					.getNature() == ParameterNature.REFERENCE) {
				editedParamValue.getValue().setValueFactory(ref);
			}
		}).addDependency(detachOnUpdateOf(editedParamValue));

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
		}).addDependency(detachOnUpdateOf(editedParamValue));

	}

	public static final IConverter<Map<String, TestParameterValue>, Collection<ParameterValue>> toListModel(
			final TestCase tc, final ObjectProperty<TestParameterValue> propertyValue) {

		return new IConverter<Map<String, TestParameterValue>, Collection<ParameterValue>>() {

			@Override
			public List<ParameterValue> convertPropertyValueToComponentValue(
					final Map<String, TestParameterValue> values) {
				final List<ParameterValue> paramList = new ArrayList<>();
				for (final Entry<String, TestParameterValue> value : values.entrySet()) {
					paramList.add(
							asParam(tc, value.getKey(), value.getValue(), propertyValue.getValue().getValueFactory()));
				}
				return paramList;
			}

			@Override
			public Map<String, TestParameterValue> convertComponentValueToPropertyValue(
					final Collection<ParameterValue> componentValue) {
				final Map<String, TestParameterValue> result = new HashMap<>();
				for (final ParameterValue pv : componentValue) {
					if (!pv.enabled && !pv.mandatory) {
						continue;
					}
					result.put(pv.name, new TestParameterValue(pv.name, TestParameterFactory.NO_FACTORY, pv.value));
				}
				return result;
			}

		};

	}

	private static void updateParam(final ParameterValue paramValue, final TestParameterFactory factory) {
		final boolean mandatory = factory.hasMandatoryParameter(paramValue.name);
		final boolean optional = factory.hasOptionalParameter(paramValue.name);
		paramValue.update(mandatory, mandatory || optional);
	}

	private static ParameterValue asParam(final TestCase tc, final String complexParameterId,
			final TestParameterValue complexValue, final TestParameterFactory factory) {
		final String simpleValue = complexValue.getSimpleValue();
		final ParameterValue paramValue = new ParameterValue(complexParameterId, complexValue.getValueFactory(),
				tc.descriptionOf(complexParameterId).getDescription(), simpleValue,
				!Strings.isNullOrEmpty(simpleValue));
		updateParam(paramValue, factory);
		return paramValue;
	}

}
