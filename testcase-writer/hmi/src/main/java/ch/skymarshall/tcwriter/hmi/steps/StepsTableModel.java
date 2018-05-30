package ch.skymarshall.tcwriter.hmi.steps;

import java.util.List;

import org.skymarshall.hmi.model.ListModel;
import org.skymarshall.hmi.swing.model.ListModelTableModel;

import ch.skymarshall.tcwriter.generators.Helper.Reference;
import ch.skymarshall.tcwriter.generators.model.IdObject;
import ch.skymarshall.tcwriter.generators.model.TestAction;
import ch.skymarshall.tcwriter.generators.model.TestCase;
import ch.skymarshall.tcwriter.generators.model.TestModel;
import ch.skymarshall.tcwriter.generators.model.TestParameter;
import ch.skymarshall.tcwriter.generators.model.TestParameter.ParameterNature;
import ch.skymarshall.tcwriter.generators.model.TestParameterType;
import ch.skymarshall.tcwriter.generators.model.TestParameterValue;
import ch.skymarshall.tcwriter.generators.model.TestStep;

public class StepsTableModel extends ListModelTableModel<TestStep, StepsTableModel.Column> {

	private final TestModel model;
	private final ListModel<TestStep> steps;
	private final TestCase tc;

	public enum Column {
		STEP, ACTOR, METHOD, PARAM0, PARAM1, TO_VALUE
	}

	public StepsTableModel(final ListModel<TestStep> steps, final TestCase tc) {
		super(steps, Column.class);
		this.steps = steps;
		this.tc = tc;
		this.model = tc.getModel();
	}

	@Override
	protected Object getValueAtColumn(final TestStep testStep, final Column column) {
		IdObject tcObject;
		final ParameterNature nature = ParameterNature.TEST_API_TYPE;
		final List<TestParameterValue> parametersValue = testStep.getParametersValue();
		switch (column) {
		case STEP:
			return testStep.getOrdinal();
		case ACTOR:
			tcObject = testStep.getActor();
			break;
		case METHOD:
			tcObject = testStep.getAction();
			break;
		case PARAM0:
			if (parametersValue.isEmpty()) {
				return "";
			}
			return createReferenceFromParam(parametersValue.get(0));
		case PARAM1:
			if (parametersValue.size() < 2) {
				return "N/A";
			}
			return createReferenceFromParam(parametersValue.get(1));
		case TO_VALUE:
			if (testStep.getReference() != null) {
				return testStep.getReference().getName();
			}
			return "N/A";
		default:
			return "N/A";
		}
		return new Reference(tcObject.getId(), model.descriptionOf(tcObject), nature);
	}

	private Object createReferenceFromParam(final TestParameterValue parameterValue) {
		final TestParameter parameterDef = parameterValue.getTestParameter();
		String display;
		switch (parameterDef.getNature()) {
		case REFERENCE:
			display = model.descriptionOf(parameterValue.getId());
			break;
		case SIMPLE_TYPE:
			display = parameterValue.getSimpleValue();
			break;
		case TEST_API_TYPE:
			display = model.descriptionOf(parameterDef);
			break;
		default:
			display = "N/A";
		}
		return new Reference(parameterDef.getId(), display, parameterDef.getNature());
	}

	@Override
	public boolean isCellEditable(final int rowIndex, final int columnIndex) {
		final TestStep step = steps.getValueAt(rowIndex);
		final Column column = columnOf(columnIndex);
		switch (column) {
		case ACTOR:
		case METHOD:
			return true;
		case PARAM0:
			return step.getParametersValue().size() > 0;
		case PARAM1:
			return step.getParametersValue().size() > 1;
		case TO_VALUE:
			return true;
		default:
			return false;
		}
	}

	@Override
	public void setValueAt(final Object value, final int rowIndex, final int columnIndex) {

		if (value == null) {
			return;
		}

		final TestStep step = steps.getValueAt(rowIndex);
		final Reference reference = (Reference) value;
		final String newId = reference.getId();
		final Column column = columnOf(columnIndex);
		switch (column) {
		case ACTOR:
			step.setActor(model.getActors().get(newId));
			step.setAction(TestAction.NOT_SET);
			step.getParametersValue().clear();
			return;
		case METHOD:
			final TestAction oldMethod = step.getAction();
			step.setAction(step.getRole().getApi(newId));

			for (int i = 0; i < step.getParametersValue().size(); i++) {
				final TestParameterValue newMethodParameter = step.getParametersValue().get(i);
				if (i >= oldMethod.getParameters().size()) {
					step.addParameter(TestParameterValue.NO_VALUE);
				} else if (newMethodParameter.getTestParameter().getType()
						.equals(oldMethod.getParameters().get(i).getType())) {
					step.getParametersValue().set(i, TestParameterValue.NO_VALUE);
				}
			}
			for (int i = step.getParametersValue().size(); i < step.getAction().getParameters().size(); i++) {
				step.getParametersValue().add(TestParameterValue.NO_VALUE);
			}
			return;
		case PARAM0:
			step.getParametersValue().set(0, createParameterValue(reference, step.getAction().getParameter(0)));
			return;
		case PARAM1:
			step.getParametersValue().set(1, createParameterValue(reference, step.getAction().getParameter(1)));
			return;
		case TO_VALUE:
			step.getReference().rename((String) value);
			break;
		default:
		}

	}

	private TestParameterValue createParameterValue(final Reference reference,
			final TestParameterType testParameterType) {
		switch (reference.getNature()) {
		case SIMPLE_TYPE:
			return new TestParameterValue(testParameterType.getId(), testParameterType.asParameter(),
					reference.getDisplay());
		case REFERENCE:
			return new TestParameterValue(reference.getId(), tc.getReference(reference.getId()),
					reference.getDisplay());
		case TEST_API_TYPE:
			return new TestParameterValue(reference.getId(), model.getTestParameterFactory(reference.getId()));
		default:
			return TestParameterValue.NO_VALUE;
		}

	}

	@Override
	public String getColumnName(final int column) {
		return Column.values()[column].name();
	}

	@Override
	protected void setValueAtColumn(final TestStep object, final Column column, final Object value) {
		// TODO Auto-generated method stub

	}

}
