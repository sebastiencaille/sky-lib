package ch.skymarshall.tcwriter.gui.editors.params;

import static ch.skymarshall.gui.mvc.ChainDependencies.detachOnUpdateOf;
import static ch.skymarshall.gui.mvc.converters.Converters.filter;
import static ch.skymarshall.gui.mvc.converters.Converters.listConverter;
import static ch.skymarshall.gui.swing.bindings.SwingBindings.selection;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
import ch.skymarshall.gui.mvc.ControllerPropertyChangeSupport;
import ch.skymarshall.gui.mvc.converters.Converters;
import ch.skymarshall.gui.mvc.converters.IConverter;
import ch.skymarshall.gui.mvc.properties.ListProperty;
import ch.skymarshall.gui.mvc.properties.ObjectProperty;
import ch.skymarshall.gui.swing.bindings.SwingBindings;
import ch.skymarshall.tcwriter.generators.Helper.VerbatimValue;
import ch.skymarshall.tcwriter.generators.model.testapi.TestApiParameter;
import ch.skymarshall.tcwriter.generators.model.testapi.TestParameterDefinition;
import ch.skymarshall.tcwriter.generators.model.testapi.TestParameterDefinition.ParameterNature;
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
			return obj instanceof TestReference && ((TestReference) obj).getId().equals(reference.getId());
		}

		@Override
		public String toString() {
			return reference.toDescription().getStepSummary();
		}

		public static IConverter<TestReference, ReferenceView> converter() {
			return Converters.converter(ReferenceView::new, ReferenceView::getReference);
		}

	}

	private List<TestReference> getReferences(final TestCase testCase, final TestParameterValue testParameterValue) {
		if (testParameterValue.getApiParameterId().isEmpty()) {
			return Collections.emptyList();
		}
		return new ArrayList<>(testCase.getReferences(typeOf(testCase, testParameterValue)));
	}

	private TestApiParameter typeOf(final TestCase testCase, final TestParameterValue testParameterValue) {
		return testCase.getTypeOf(testParameterValue.getApiParameterId());
	}

	public TestParameterValueEditorPanel(final String name, final ControllerPropertyChangeSupport propertyChangeSupport,
			final ObjectProperty<TestCase> tc, final ObjectProperty<TestParameterValue> editedParameterValue,
			final ObjectProperty<TestParameterDefinition> testApi) {

		final ObjectProperty<TestParameterDefinition.ParameterNature> valueNature = new ObjectProperty<>(
				name + "-nature", propertyChangeSupport);
		final ObjectProperty<String> simpleValue = editedParameterValue.child(name + "-simpleValue",
				TestParameterValue::getSimpleValue, TestParameterValue::setSimpleValue);
		final ObjectProperty<TestReference> reference = new ObjectProperty<>(name + "-reference",
				propertyChangeSupport);

		final ListProperty<TestReference> references = new ListProperty<>(name + "-references", propertyChangeSupport);

		tc.addListener(l -> references.setValue(this, getReferences(tc.getValue(), editedParameterValue.getValue())));
		editedParameterValue.addListener(
				l -> references.setValue(this, getReferences(tc.getValue(), editedParameterValue.getValue())));

		setLayout(new BorderLayout());

		final JPanel topPanel = new JPanel();
		topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.PAGE_AXIS));

		final JRadioButton useRawValue = new JRadioButton("Raw value");
		topPanel.add(useRawValue);
		final JTextField simpleValueEditor = new JTextField();
		simpleValue.bind(SwingBindings.value(simpleValueEditor));
		topPanel.add(simpleValueEditor);

		final JRadioButton useReference = new JRadioButton("Reference: ");
		topPanel.add(useReference);
		final JComboBox<ReferenceView> referenceEditor = new JComboBox<>();
		references.bind(filter(r -> r.getType().equals(editedParameterValue.getValue().getValueDefinition().getType())))
				.bind(listConverter(ReferenceView.converter())).bind(SwingBindings.values(referenceEditor));
		reference.bind(ReferenceView.converter()).bind(selection(referenceEditor))
				.addDependency(detachOnUpdateOf(references));
		topPanel.add(referenceEditor);

		final JRadioButton useComplexType = new JRadioButton("Parameters: ");
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

		editedParameterValue.addListener(
				l -> valueNature.setValue(this, editedParameterValue.getValue().getValueDefinition().getNature()));

		reference.addListener(l -> {
			if (valueNature.getValue() == ParameterNature.REFERENCE) {
				editedParameterValue.getValue().setValueDefinition(reference.getObjectValue());
			}
		});

		valueNature.addListener(l -> {
			final TestParameterValue paramValue = editedParameterValue.getValue();
			switch (valueNature.getValue()) {
			case SIMPLE_TYPE:
				paramValue.setValueDefinition(typeOf(tc.getValue(), paramValue).asSimpleParameter());
				break;
			case REFERENCE:
				paramValue.setValueDefinition(reference.getObjectValue());
				break;
			case TEST_API:
				paramValue.setValueDefinition(testApi.getValue());
				break;
			default:
				break;
			}
		});

	}

	public static final Collection<ParameterValue> toListModel(final TestCase tc,
			final TestParameterValue parameterValue) {
		final List<ParameterValue> paramList = new ArrayList<>();

		for (final TestApiParameter mandatoryParameter : parameterValue.getValueDefinition().getMandatoryParameters()) {
			paramList.add(asParam(tc, mandatoryParameter, parameterValue, true));
		}

		for (final TestApiParameter optionalParameter : parameterValue.getValueDefinition().getOptionalParameters()) {
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

	public static JComboBox<VerbatimValue> prepareFastListEditor(final List<VerbatimValue>... references) {
		return new JComboBox<>(Arrays.stream(references).flatMap(Collection::stream).collect(Collectors.toList())
				.toArray(new VerbatimValue[0]));
	}
}