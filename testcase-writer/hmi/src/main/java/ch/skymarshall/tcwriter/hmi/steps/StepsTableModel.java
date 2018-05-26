package ch.skymarshall.tcwriter.hmi.steps;

import org.skymarshall.hmi.model.ListModel;
import org.skymarshall.hmi.swing.model.ListModelTableModel;

import ch.skymarshall.tcwriter.generators.model.TestModel;
import ch.skymarshall.tcwriter.generators.model.TestStep;

public class StepsTableModel extends ListModelTableModel<TestStep, StepsTableModel.Column> {

	private final TestModel model;

	public enum Column {
		ACTOR, METHOD, SELECTOR, PARAMS
	}

	public StepsTableModel(final ListModel<TestStep> steps, final TestModel model) {
		super(steps, Column.class);
		this.model = model;
	}

	@Override
	protected Object getValueAtColumn(final TestStep object, final Column column) {
		switch (column) {
		case ACTOR:
			return model.getDescriptions().get(object.getActor().getId());
		case METHOD:
			return model.getDescriptions().get(object.getMethod().getId());
		case SELECTOR:
			if (object.getParameters().isEmpty()) {
				return "";
			}
			return model.getDescriptions().get(object.getParameters().get(0).getTestObject().getId());
		case PARAMS:
			if (object.getParameters().size() < 2) {
				return "";
			}
			return model.getDescriptions().get(object.getParameters().get(1).getTestObject().getId());
		default:
			return "";
		}
	}

	@Override
	public boolean isCellEditable(final int rowIndex, final int columnIndex) {
		return true;
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
