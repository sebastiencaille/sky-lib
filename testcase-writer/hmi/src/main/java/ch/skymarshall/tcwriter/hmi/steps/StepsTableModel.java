package ch.skymarshall.tcwriter.hmi.steps;

import org.skymarshall.hmi.model.ListModel;
import org.skymarshall.hmi.swing.model.ListModelTableModel;

import ch.skymarshall.tcwriter.generators.Helper.Reference;
import ch.skymarshall.tcwriter.generators.model.IdObject;
import ch.skymarshall.tcwriter.generators.model.TestMethod;
import ch.skymarshall.tcwriter.generators.model.TestModel;
import ch.skymarshall.tcwriter.generators.model.TestObjectParameter;
import ch.skymarshall.tcwriter.generators.model.TestStep;
import ch.skymarshall.tcwriter.generators.model.TestValue;

public class StepsTableModel extends ListModelTableModel<TestStep, StepsTableModel.Column> {

	private final TestModel model;
	private final ListModel<TestStep> steps;

	public enum Column {
		ACTOR, METHOD, SELECTOR, PARAMS
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
			tcObject = testStep.getMethod();
			break;
		case SELECTOR:
			if (testStep.getParameters().isEmpty()) {
				return "";
			}
			tcObject = testStep.getParameters().get(0).getTestObject();
			break;
		case PARAMS:
			if (testStep.getParameters().size() < 2) {
				return "N/A";
			}
			tcObject = testStep.getParameters().get(1).getTestObject();
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
		case SELECTOR:
			return step.getParameters().size() > 0;
		case PARAMS:
			return step.getParameters().size() > 1;
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
			step.setMethod(TestMethod.NO_METHOD);
			step.getParameters().clear();
			return;
		case METHOD:
			final TestMethod oldMethod = step.getMethod();
			step.setMethod(step.getRole().getApi(newId));

			for (int i = 0; i < step.getParameters().size(); i++) {
				final TestValue newMethodParameter = step.getParameters().get(i);
				if (i >= oldMethod.getParameters().size()) {
					step.addParameter(TestValue.NO_VALUE);
				} else if (newMethodParameter.getTestObject().getType()
						.equals(oldMethod.getParameters().get(i).getType())) {
					step.getParameters().set(i, TestValue.NO_VALUE);
				}
			}
			for (int i = step.getParameters().size(); i < step.getMethod().getParameters().size(); i++) {
				step.getParameters().add(TestValue.NO_VALUE);
			}
			return;
		case SELECTOR:
			TestObjectParameter ObjectParam0 = step.getMethod().getParameters().get(0);
			final TestValue newTestValue0 = new TestValue(ObjectParam0.getId(),
					model.getTestObject(ObjectParam0, newId));
			step.getParameters().set(0, newTestValue0);
			return;
		case PARAMS:
			TestObjectParameter objectParam1 = step.getMethod().getParameters().get(1);
			final TestValue newTestValue1 = new TestValue(objectParam1.getId(),
					model.getTestObject(objectParam1, newId));
			step.getParameters().set(1, newTestValue1);
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
