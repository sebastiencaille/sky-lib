package ch.skymarshall.tcwriter.hmi.steps;

import org.skymarshall.hmi.model.ListModel;
import org.skymarshall.hmi.swing.model.ListModelTableModel;

import ch.skymarshall.tcwriter.generators.Helper.Reference;
import ch.skymarshall.tcwriter.generators.model.IdObject;
import ch.skymarshall.tcwriter.generators.model.TestAction;
import ch.skymarshall.tcwriter.generators.model.TestModel;
import ch.skymarshall.tcwriter.generators.model.TestParameterType;
import ch.skymarshall.tcwriter.generators.model.TestParameterValue;
import ch.skymarshall.tcwriter.generators.model.TestStep;

public class StepsTableModel extends ListModelTableModel<TestStep, StepsTableModel.Column> {

	private final TestModel model;
	private final ListModel<TestStep> steps;

	public enum Column {
		ACTOR, METHOD, PARAM0, PARAM1
	}

	public StepsTableModel(final ListModel<TestStep> steps, final TestModel model) {
		super(steps, Column.class);
		this.steps = steps;
		this.model = model;
	}

	@Override
	protected Object getValueAtColumn(final TestStep testStep, final Column column) {
		IdObject tcObject;
		switch (column) {
		case ACTOR:
			tcObject = testStep.getActor();
			break;
		case METHOD:
			tcObject = testStep.getAction();
			break;
		case PARAM0:
			if (testStep.getParametersValue().isEmpty()) {
				return "";
			}
			tcObject = testStep.getParametersValue().get(0).getTestParameter();
			break;
		case PARAM1:
			if (testStep.getParametersValue().size() < 2) {
				return "N/A";
			}
			tcObject = testStep.getParametersValue().get(1).getTestParameter();
			break;
		default:
			return "N/A";
		}
		return model.descriptionOf(tcObject);
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
		default:
			return false;
		}
	}

	@Override
	public void setValueAt(final Object reference, final int rowIndex, final int columnIndex) {

		if (reference == null) {
			return;
		}

		final TestStep step = steps.getValueAt(rowIndex);
		final String newId = ((Reference) reference).getId();
		final Column column = columnOf(columnIndex);
		switch (column) {
		case ACTOR:
			step.setActor(model.getActors().get(newId));
			step.setAction(TestAction.NO_METHOD);
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
			final TestParameterType objectParam0 = step.getAction().getParameters().get(0);
			final TestParameterValue newTestValue0 = new TestParameterValue(objectParam0.getId(),
					model.getTestParameter(newId));
			step.getParametersValue().set(0, newTestValue0);
			return;
		case PARAM1:
			final TestParameterType objectParam1 = step.getAction().getParameters().get(1);
			final TestParameterValue newTestValue1 = new TestParameterValue(objectParam1.getId(),
					model.getTestParameter(newId));
			step.getParametersValue().set(1, newTestValue1);
			return;
		default:
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
