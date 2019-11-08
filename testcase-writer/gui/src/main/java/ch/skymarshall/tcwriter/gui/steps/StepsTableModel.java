package ch.skymarshall.tcwriter.gui.steps;

import java.util.List;

import javax.swing.event.TableModelEvent;

import ch.skymarshall.gui.model.ListModel;
import ch.skymarshall.gui.mvc.properties.ObjectProperty;
import ch.skymarshall.gui.swing.model.ListModelTableModel;
import ch.skymarshall.tcwriter.generators.Helper.VerbatimValue;
import ch.skymarshall.tcwriter.generators.model.IdObject;
import ch.skymarshall.tcwriter.generators.model.ModelUtils;
import ch.skymarshall.tcwriter.generators.model.ModelUtils.ActionUtils;
import ch.skymarshall.tcwriter.generators.model.testapi.TestAction;
import ch.skymarshall.tcwriter.generators.model.testapi.TestParameterFactory;
import ch.skymarshall.tcwriter.generators.model.testapi.TestParameterFactory.ParameterNature;
import ch.skymarshall.tcwriter.generators.model.testcase.TestCase;
import ch.skymarshall.tcwriter.generators.model.testcase.TestParameterValue;
import ch.skymarshall.tcwriter.generators.model.testcase.TestStep;
import ch.skymarshall.tcwriter.generators.visitors.HumanReadableVisitor;
import ch.skymarshall.tcwriter.gui.TestRemoteControl;

public class StepsTableModel extends ListModelTableModel<TestStep, StepsTableModel.Column> {

	private final ListModel<TestStep> steps;
	private final TestRemoteControl testControl;
	private final ObjectProperty<TestCase> testCaseProperty;

	public enum Column {
		BREAKPOINT, STEP, ACTOR, ACTION, SELECTOR, PARAM0, TO_VALUE
	}

	private HumanReadableVisitor summaryVisitor;

	public StepsTableModel(final ObjectProperty<TestCase> testCaseProperty, final ListModel<TestStep> steps,
			final TestRemoteControl testControl) {
		super(steps, Column.class);
		this.testCaseProperty = testCaseProperty;
		this.steps = steps;
		this.testControl = testControl;
		testCaseProperty
				.addListener(evt -> summaryVisitor = new HumanReadableVisitor(testCaseProperty.getObjectValue()));
	}

	@Override
	protected Object getValueAtColumn(final TestStep testStep, final Column column) {
		final TestCase tc = testCaseProperty.getValue();

		IdObject tcObject;
		final ParameterNature nature = ParameterNature.TEST_API;
		final List<TestParameterValue> parametersValue = testStep.getParametersValue();
		final TestAction testAction = testStep.getAction();
		final ActionUtils actionUtils = ModelUtils.actionUtils(tc.getModel(), testAction);
		switch (column) {
		case BREAKPOINT:
			return testControl.stepStatus(testStep.getOrdinal());
		case STEP:
			return testStep.getOrdinal();
		case ACTOR:
			tcObject = testStep.getActor();
			break;
		case ACTION:
			tcObject = testAction;
			break;
		case SELECTOR:
			if (!actionUtils.hasSelector()) {
				return "";
			}
			return createReferenceFromParam(tc, parametersValue.get(actionUtils.selectorIndex()));
		case PARAM0:
			if (actionUtils.hasActionParameter(0)) {
				return createReferenceFromParam(tc, parametersValue.get(actionUtils.actionParameterIndex(0)));
			}
			return "";
		case TO_VALUE:
			if (testStep.getReference() != null) {
				return testStep.getReference().getName();
			}
			return "";
		default:
			return "";
		}
		return new VerbatimValue(tcObject.getId(), tc.descriptionOf(tcObject).getDescription(), nature);
	}

	private Object createReferenceFromParam(final TestCase tc, final TestParameterValue parameterValue) {
		final TestParameterFactory parameterDef = parameterValue.getValueFactory();
		String display;
		switch (parameterDef.getNature()) {
		case REFERENCE:
			display = tc.descriptionOf(parameterValue.getSimpleValue()).getDescription();
			break;
		case SIMPLE_TYPE:
			display = parameterValue.getSimpleValue();
			break;
		case TEST_API:
			display = tc.descriptionOf(parameterDef).getDescription();
			break;
		default:
			display = "N/A";
		}
		return new VerbatimValue(parameterDef.getId(), display, parameterDef.getNature());
	}

	@Override
	public boolean isCellEditable(final int rowIndex, final int columnIndex) {
		if (rowIndex % 2 == 0) {
			return false;
		}
		return columnOf(columnIndex) == Column.BREAKPOINT;
	}

	@Override
	public String getColumnName(final int column) {
		return Column.values()[column].name();
	}

	@Override
	public Object getValueAt(final int row, final int column) {
		final TestStep step = getObjectAtRow(row);
		if (row % 2 == 0) {
			return summaryVisitor.process(step);
		}
		return getValueAtColumn(step, columnOf(column));
	}

	@Override
	public TestStep getObjectAtRow(final int row) {
		return super.getObjectAtRow(row / 2);
	}

	@Override
	public void setValueAt(final Object aValue, final int row, final int column) {
		setValueAtColumn(getObjectAtRow(row), columnOf(column), aValue);
		fireTableRowsUpdated(row - 1, row);
	}

	@Override
	protected void setValueAtColumn(final TestStep testStep, final Column column, final Object value) {
		if (value == null) {
			return;
		}
		final TestCase tc = testCaseProperty.getValue();

		switch (column) {
		case BREAKPOINT:
			if ((Boolean) value) {
				testControl.addBreakpoint(testStep);
			} else {
				testControl.removeBreakpoint(testStep);
			}
			return;
		case TO_VALUE:
			tc.publishReference(testStep.getReference().rename((String) value, "TODO"));
			return;
		default:
			break;
		}
	}

	public void stepExecutionUpdated(final int first, final int last) {
		final int min = first - 1;
		final int max = Math.max(last - 1, steps.getSize());
		fireTableChanged(new TableModelEvent(this, min, max, Column.BREAKPOINT.ordinal()));
	}

	@Override
	public int getRowCount() {
		return getBaseModel().getSize() * 2;
	}
}
