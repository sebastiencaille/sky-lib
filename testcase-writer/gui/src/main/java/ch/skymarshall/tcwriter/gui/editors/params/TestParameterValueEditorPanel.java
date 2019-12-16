package ch.skymarshall.tcwriter.gui.editors.params;

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
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import ch.skymarshall.gui.model.ListModel;
import ch.skymarshall.gui.model.ListModelBindings;
import ch.skymarshall.gui.model.RootListModel;
import ch.skymarshall.gui.model.views.ListViews;
import ch.skymarshall.gui.mvc.converters.IConverter;
import ch.skymarshall.gui.mvc.properties.ObjectProperty;
import ch.skymarshall.gui.swing.bindings.ObjectTextView;
import ch.skymarshall.tcwriter.generators.model.testapi.TestApiParameter;
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

		final ListModel<ParameterValue> editedParameters = new RootListModel<>(ListViews.<ParameterValue>sorted());
		final TestParameterValueTable view = new TestParameterValueTable(
				new TestParameterValueTableModel(editedParameters));
		editedParamValue.bind(v -> toListModel(tc.getValue(), v)).bind(ListModelBindings.values(editedParameters));

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

	public static final Collection<ParameterValue> toListModel(final TestCase tc,
			final TestParameterValue parameterValue) {
		final List<ParameterValue> paramList = new ArrayList<>();

		for (final TestApiParameter mandatoryParameter : parameterValue.getValueFactory().getMandatoryParameters()) {
			paramList.add(asParam(tc, mandatoryParameter, parameterValue, true));
		}

		for (final TestApiParameter optionalParameter : parameterValue.getValueFactory().getOptionalParameters()) {
			paramList.add(asParam(tc, optionalParameter, parameterValue, false));
		}
		return paramList;
	}

	private static ParameterValue asParam(final TestCase tc, final TestApiParameter complexParameter,
			final TestParameterValue complexParameterValue, final boolean mandatory) {
		final String complexParameterId = complexParameter.getId();
		final TestParameterValue testParameterValue = complexParameterValue.getComplexTypeValues()
				.get(complexParameterId);
		return new ParameterValue(complexParameterId, complexParameter.asSimpleParameter(),
				complexParameterValue.getComplexTypeValues().containsKey(complexParameterId),
				tc.descriptionOf(complexParameterId).getDescription(),
				(testParameterValue != null) ? testParameterValue.getSimpleValue() : "", mandatory);
	}

}
