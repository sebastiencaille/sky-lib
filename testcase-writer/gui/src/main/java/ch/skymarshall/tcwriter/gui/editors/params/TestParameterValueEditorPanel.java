package ch.skymarshall.tcwriter.gui.editors.params;

import static ch.skymarshall.gui.mvc.ChainDependencies.detachOnUpdateOf;
import static ch.skymarshall.gui.mvc.converters.Converters.filter;
import static ch.skymarshall.gui.mvc.converters.Converters.listConverter;
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
import ch.skymarshall.gui.mvc.ChainDependencies;
import ch.skymarshall.gui.mvc.IScopedSupport;
import ch.skymarshall.gui.mvc.converters.Converters;
import ch.skymarshall.gui.mvc.converters.IConverter;
import ch.skymarshall.gui.mvc.properties.ListProperty;
import ch.skymarshall.gui.mvc.properties.ObjectProperty;
import ch.skymarshall.gui.swing.bindings.SwingBindings;
import ch.skymarshall.tcwriter.generators.model.testapi.TestApiParameter;
import ch.skymarshall.tcwriter.generators.model.testapi.TestParameterFactory;
import ch.skymarshall.tcwriter.generators.model.testapi.TestParameterFactory.ParameterNature;
import ch.skymarshall.tcwriter.generators.model.testcase.TestCase;
import ch.skymarshall.tcwriter.generators.model.testcase.TestParameterValue;
import ch.skymarshall.tcwriter.generators.model.testcase.TestReference;
import ch.skymarshall.tcwriter.gui.editors.params.TestParameterValueTableModel.ParameterValue;

public class TestParameterValueEditorPanel extends JPanel {

	public static class ReferenceView {
		private final TestReference reference;

		public ReferenceView(final TestReference reference) {
			this.reference = reference;
		}

		public TestReference getReference() {
			return reference;
		}

		@Override
		public boolean equals(final Object obj) {
			return obj instanceof TestReference && ((TestReference) obj).equals(reference);
		}

		@Override
		public int hashCode() {
			return reference.hashCode();
		}

		@Override
		public String toString() {
			return reference.toDescription().getDescription();
		}

		public static IConverter<TestReference, ReferenceView> converter() {
			return Converters.converter(ReferenceView::new, ReferenceView::getReference);
		}

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

	public TestParameterValueEditorPanel(final String name, final IScopedSupport propertyChangeSupport,
			final ObjectProperty<TestCase> tc, final ObjectProperty<TestParameterValue> editedParameterValue,
			final ObjectProperty<TestParameterFactory> testApi) {

		final ObjectProperty<TestParameterFactory.ParameterNature> valueNature = new ObjectProperty<>(name + "-nature",
				propertyChangeSupport);
		final ObjectProperty<String> simpleValue = editedParameterValue.child(name + "-simpleValue",
				TestParameterValue::getSimpleValue, TestParameterValue::setSimpleValue);
		final ObjectProperty<TestReference> reference = new ObjectProperty<>(name + "-reference",
				propertyChangeSupport);

		final ListProperty<TestReference> references = new ListProperty<>(name + "-references", propertyChangeSupport);

		tc.listen(test -> references.setValue(this, getReferences(test, editedParameterValue.getValue())));
		editedParameterValue.listen(values -> references.setValue(this, getReferences(tc.getValue(), values)));

		setLayout(new BorderLayout());

		final JPanel topPanel = new JPanel();
		topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.PAGE_AXIS));

		final JRadioButton useRawValue = new JRadioButton("Raw value");
		topPanel.add(useRawValue);
		final JTextField simpleValueEditor = new JTextField();
		simpleValue.bind(value(simpleValueEditor));
		topPanel.add(simpleValueEditor);

		final JRadioButton useReference = new JRadioButton("Reference: ");
		topPanel.add(useReference);
		final JComboBox<ReferenceView> referenceEditor = new JComboBox<>();
		references.bind(filter(r -> r.getType().equals(editedParameterValue.getValue().getValueFactory().getType()))) //
				.bind(listConverter(ReferenceView.converter())) //
				.bind(values(referenceEditor));
		reference.bind(ReferenceView.converter())//
				.bind(selection(referenceEditor)) //
				.addDependency(detachOnUpdateOf(references));
		topPanel.add(referenceEditor);

		final JRadioButton useComplexType = new JRadioButton("Test Api: ");
		topPanel.add(useComplexType);
		add(topPanel, BorderLayout.NORTH);

		final ListModel<ParameterValue> editedParameters = new RootListModel<>(ListViews.<ParameterValue>sorted());
		final TestParameterValueTable view = new TestParameterValueTable(
				new TestParameterValueTableModel(editedParameters));
		editedParameterValue.bind(v -> toListModel(tc.getValue(), v)).bind(ListModelBindings.values(editedParameters));

		add(new JScrollPane(view), BorderLayout.CENTER);

		final ButtonGroup group = new ButtonGroup();
		group.add(useRawValue);
		group.add(useReference);
		group.add(useComplexType);

		valueNature.bind(SwingBindings.group(group, ParameterNature.REFERENCE, useReference,
				ParameterNature.SIMPLE_TYPE, useRawValue, ParameterNature.TEST_API, useComplexType));

		editedParameterValue.listen(value -> {
			valueNature.setValue(this, value.getValueFactory().getNature());
			if (value.getValueFactory().getNature() == ParameterNature.REFERENCE) {
				reference.setValue(this, (TestReference) value.getValueFactory());
			}
		});

		reference.listen(ref -> {
			if (valueNature.getValue() == ParameterNature.REFERENCE) {
				editedParameterValue.getValue().setValueFactory(ref);
			}
		});

		valueNature.listen(v -> {
			final TestParameterValue paramValue = editedParameterValue.getValue();
			switch (v) {
			case SIMPLE_TYPE:
				paramValue.setValueFactory(apiOf(tc.getValue(), paramValue).asSimpleParameter());
				break;
			case REFERENCE:
				paramValue.setValueFactory(reference.getObjectValue());
				break;
			case TEST_API:
				paramValue.setValueFactory(testApi.getValue());
				break;
			default:
				break;
			}
		}).addDependency(ChainDependencies.detachOnUpdateOf(editedParameterValue));

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
