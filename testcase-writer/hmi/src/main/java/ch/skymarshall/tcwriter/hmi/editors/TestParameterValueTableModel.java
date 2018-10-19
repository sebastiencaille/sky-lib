package ch.skymarshall.tcwriter.hmi.editors;

import java.util.Comparator;

import org.skymarshall.hmi.model.ListModel;
import org.skymarshall.hmi.model.RootListModel;
import org.skymarshall.hmi.model.views.ListViews;
import org.skymarshall.hmi.swing.model.ListModelTableModel;

import ch.skymarshall.tcwriter.generators.model.testapi.TestParameter;
import ch.skymarshall.tcwriter.generators.model.testapi.TestParameterType;
import ch.skymarshall.tcwriter.generators.model.testcase.TestCase;
import ch.skymarshall.tcwriter.generators.model.testcase.TestParameterValue;

public class TestParameterValueTableModel
		extends ListModelTableModel<TestParameterValueTableModel.ParameterValue, TestParameterValueTableModel.Columns> {

	public enum Columns {
		ENABLED, DESCRIPTION, VALUE
	}

	static class ParameterValue {
		final String id;
		final TestParameter parameterDefinition;
		final String description;
		final boolean mandatory;
		boolean enabled;
		String value;

		ParameterValue(final String id, final TestParameter parameterDefinition, final boolean enabled,
				final String description, final String value, final boolean mandatory) {
			this.id = id;
			this.parameterDefinition = parameterDefinition;
			this.enabled = enabled || mandatory;
			this.description = description;
			this.value = value;
			this.mandatory = mandatory;

		}
	}

	private static ParameterValue asParam(final TestCase tc, final TestParameter parameterFactory,
			final TestParameterType complexParameter, final TestParameterValue complexParameterValue,
			final boolean mandatory) {
		final String complexParameterId = complexParameter.getId();
		final TestParameterValue testParameterValue = complexParameterValue.getComplexTypeValues()
				.get(complexParameterId);
		return new ParameterValue(complexParameterId, complexParameter.asParameter(),
				complexParameterValue.getComplexTypeValues().containsKey(complexParameterId),
				tc.descriptionOf(complexParameterId).getDescription(),
				(testParameterValue != null) ? testParameterValue.getSimpleValue() : "", mandatory);
	}

	private static class ParamValueComparator implements Comparator<ParameterValue> {
		@Override
		public int compare(final ParameterValue o1, final ParameterValue o2) {
			if (o1.mandatory && !o2.mandatory) {
				return -1;
			} else if (!o1.mandatory && o2.mandatory) {
				return 1;
			}
			return o1.description.compareTo(o2.description);
		}
	}

	public static final ListModel<ParameterValue> toListModel(final TestCase tc, final TestParameter parameter,
			final TestParameterValue parameterValue) {
		final ListModel<ParameterValue> paramList = new RootListModel<>(ListViews.sorted(new ParamValueComparator()));

		for (final TestParameterType mandatoryParameter : parameter.getMandatoryParameters()) {
			paramList.insert(asParam(tc, parameter, mandatoryParameter, parameterValue, true));
		}

		for (final TestParameterType optionalParameter : parameter.getOptionalParameters()) {
			paramList.insert(asParam(tc, parameter, optionalParameter, parameterValue, false));
		}
		return paramList;
	}

	public TestParameterValueTableModel(final ListModel<ParameterValue> paramList) {
		super(paramList, Columns.class);
	}

	@Override
	protected Object getValueAtColumn(final ParameterValue object, final Columns column) {
		switch (column) {
		case ENABLED:
			return object.enabled || object.mandatory;
		case DESCRIPTION:
			return object.description;
		case VALUE:
			return object.value;
		default:
			throw new IllegalStateException("Unknown column " + column);
		}
	}

	@Override
	protected void setValueAtColumn(final ParameterValue object, final Columns column, final Object value) {
		switch (column) {
		case ENABLED:
			object.enabled = (Boolean) value;
			break;
		case VALUE:
			object.value = (String) value;
			break;
		default:
			throw new IllegalStateException("Not editable" + column);
		}

	}

}
