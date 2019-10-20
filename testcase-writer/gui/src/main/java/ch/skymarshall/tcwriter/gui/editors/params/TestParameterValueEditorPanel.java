package ch.skymarshall.tcwriter.gui.editors.params;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import ch.skymarshall.gui.model.ListModel;
import ch.skymarshall.gui.model.ListModelBindings;
import ch.skymarshall.gui.model.RootListModel;
import ch.skymarshall.gui.model.views.ListViews;
import ch.skymarshall.gui.mvc.GuiController;
import ch.skymarshall.gui.mvc.converters.Converters;
import ch.skymarshall.gui.mvc.properties.ObjectProperty;
import ch.skymarshall.gui.swing.bindings.SwingBindings;
import ch.skymarshall.tcwriter.generators.Helper.VerbatimValue;
import ch.skymarshall.tcwriter.generators.model.testapi.TestApiParameter;
import ch.skymarshall.tcwriter.generators.model.testcase.TestCase;
import ch.skymarshall.tcwriter.generators.model.testcase.TestParameterValue;
import ch.skymarshall.tcwriter.gui.editors.params.TestParameterValueTableModel.ParameterValue;

public class TestParameterValueEditorPanel extends JPanel {

	public TestParameterValueEditorPanel(final TestCase tc, final GuiController mainController,
			final ObjectProperty<TestParameterValue> editedParameterValue) {

		setLayout(new BorderLayout());

		final JComboBox<VerbatimValue> referenceEditor = new JComboBox<>();
		editedParameterValue.bind(Converters.converter(VerbatimValue::new, VerbatimValue::getValue))
				.bind(SwingBindings.selection(referenceEditor));
		add(referenceEditor, BorderLayout.NORTH);

		final ListModel<ParameterValue> editedParameters = new RootListModel<>(ListViews.<ParameterValue>sorted());
		final TestParameterValueTable view = new TestParameterValueTable(
				new TestParameterValueTableModel(editedParameters));
		editedParameterValue.bind(v -> toListModel(tc, v)).bind(ListModelBindings.values(editedParameters));

		add(new JScrollPane(view), BorderLayout.CENTER);

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
