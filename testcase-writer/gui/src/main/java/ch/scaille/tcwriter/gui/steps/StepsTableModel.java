package ch.scaille.tcwriter.gui.steps;

import java.util.List;

import javax.swing.event.TableModelEvent;

import ch.scaille.gui.model.ListModel;
import ch.scaille.gui.mvc.properties.ObjectProperty;
import ch.scaille.gui.swing.model.ListModelTableModel;
import ch.scaille.tcwriter.generators.model.IdObject;
import ch.scaille.tcwriter.generators.model.ModelUtils;
import ch.scaille.tcwriter.generators.model.ModelUtils.ActionUtils;
import ch.scaille.tcwriter.generators.model.testapi.TestAction;
import ch.scaille.tcwriter.generators.model.testapi.TestParameterFactory;
import ch.scaille.tcwriter.generators.model.testcase.TestCase;
import ch.scaille.tcwriter.generators.model.testcase.TestParameterValue;
import ch.scaille.tcwriter.generators.model.testcase.TestStep;
import ch.scaille.tcwriter.generators.visitors.HumanReadableVisitor;
import ch.scaille.tcwriter.gui.TestRemoteControl;

public class StepsTableModel extends ListModelTableModel<TestStep, StepsTableModel.Column> {

	private final TestRemoteControl testControl;
	private final ObjectProperty<TestCase> testCaseProperty;
	private HumanReadableVisitor humanReadableVisitor;

	public enum Column {
		BREAKPOINT, ORDINAL, ACTOR, ACTION, SELECTOR, PARAM0, TO_VAR
	}

	public StepsTableModel(final ObjectProperty<TestCase> testCaseProperty, final ListModel<TestStep> steps,
			final TestRemoteControl testControl) {
		super(steps, Column.class);
		this.testCaseProperty = testCaseProperty;
		this.testControl = testControl;
		testCaseProperty.listenActive(tc -> humanReadableVisitor = new HumanReadableVisitor(tc, false));
	}

	@Override
	protected Object getValueAtColumn(final TestStep testStep, final Column column) {
		final TestCase tc = testCaseProperty.getValue();

		IdObject tcObject;
		final List<TestParameterValue> parametersValue = testStep.getParametersValue();
		final TestAction testAction = testStep.getAction();
		final ActionUtils actionUtils = ModelUtils.actionUtils(tc.getDictionary(), testAction);
		switch (column) {
		case BREAKPOINT:
			return testControl.stepStatus(testStep.getOrdinal());
		case ORDINAL:
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
			return toString(tc, parametersValue.get(actionUtils.selectorIndex()));
		case PARAM0:
			if (actionUtils.hasActionParameter(0)) {
				return toString(tc, parametersValue.get(actionUtils.parameterIndex(0)));
			}
			return "";
		case TO_VAR:
			if (testStep.getReference() != null) {
				return toString(tc, testStep.getReference());
			}
			return "";
		default:
			return "";
		}
		return tc.descriptionOf(tcObject).getDescription();
	}

	private String toString(final TestCase tc, final TestParameterValue parameterValue) {
		final TestParameterFactory parameterDef = parameterValue.getValueFactory();
		String display;
		switch (parameterDef.getNature()) {
		case REFERENCE:
			display = toString(tc, parameterDef);
			break;
		case SIMPLE_TYPE:
			display = parameterValue.getSimpleValue();
			break;
		case TEST_API:
			display = toString(tc, parameterDef);
			break;
		default:
			display = "N/A";
		}
		return display;
	}

	private String toString(final TestCase tc, final TestParameterFactory parameterDef) {
		return tc.descriptionOf(parameterDef).getDescription();
	}

	@Override
	public boolean isCellEditable(final int rowIndex, final int columnIndex) {
		return columnOf(columnIndex) == Column.BREAKPOINT;
	}

	@Override
	public String getColumnName(final int column) {
		return Column.values()[column].name();
	}

	@Override
	public Object getValueAt(final int row, final int column) {
		return getValueAtColumn(getObjectAtRow(row), columnOf(column));
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
			if ((boolean) value) {
				testControl.addBreakpoint(testStep);
			} else {
				testControl.removeBreakpoint(testStep);
			}
			return;
		case TO_VAR:
			tc.publishReference(testStep.getReference().rename((String) value, "TODO"));
			return;
		default:
			break;
		}
	}

	public void stepExecutionUpdated(final int first, final int last) {
		fireTableChanged(new TableModelEvent(this, first, last, Column.BREAKPOINT.ordinal()));
	}

	@Override
	public int getRowCount() {
		return getBaseModel().getSize();
	}

	public String getHumanReadable(final int row) {
		return humanReadableVisitor.process(testCaseProperty.getValue().getSteps().get(row));
	}
}
