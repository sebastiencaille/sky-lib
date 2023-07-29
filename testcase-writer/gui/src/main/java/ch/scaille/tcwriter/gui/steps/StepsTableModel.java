package ch.scaille.tcwriter.gui.steps;

import javax.swing.event.TableModelEvent;

import ch.scaille.gui.model.ListModel;
import ch.scaille.gui.mvc.properties.ObjectProperty;
import ch.scaille.gui.swing.model.ListModelTableModel;
import ch.scaille.tcwriter.model.IdObject;
import ch.scaille.tcwriter.model.ModelUtils;
import ch.scaille.tcwriter.model.dictionary.TestParameterFactory;
import ch.scaille.tcwriter.model.testcase.TestCase;
import ch.scaille.tcwriter.model.testcase.TestParameterValue;
import ch.scaille.tcwriter.model.testcase.TestStep;
import ch.scaille.tcwriter.services.generators.visitors.HumanReadableVisitor;
import ch.scaille.tcwriter.services.testexec.TestRemoteControl;

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
		final var parametersValue = testStep.getParametersValue();
		final var testAction = testStep.getAction();
		final var actionUtils = ModelUtils.actionUtils(tc.getDictionary(), testAction);
		return switch (column) {
		case BREAKPOINT:
			yield testControl.stepStatus(testStep.getOrdinal());
		case ORDINAL:
			yield testStep.getOrdinal();
		case ACTOR:
			tcObject = testStep.getActor();
			yield tc.descriptionOf(tcObject).getDescription();
		case ACTION:
			tcObject = testAction;
			yield tc.descriptionOf(tcObject).getDescription();
		case SELECTOR:
			if (!actionUtils.hasSelector()) {
				yield "";
			}
			yield toString(tc, parametersValue.get(actionUtils.selectorIndex()));
		case PARAM0:
			if (actionUtils.hasActionParameter(0)) {
				yield toString(tc, parametersValue.get(actionUtils.parameterIndex(0)));
			}
			yield "";
		case TO_VAR:
			if (testStep.getReference() != null) {
				yield toString(tc, testStep.getReference());
			}
			yield "";
		default:
			yield "";
		};
	}

	private String toString(final TestCase tc, final TestParameterValue parameterValue) {
		final var parameterDef = parameterValue.getValueFactory();

		return switch (parameterDef.getNature()) {
		case REFERENCE -> toString(tc, parameterDef);
		case SIMPLE_TYPE -> parameterValue.getSimpleValue();
		case TEST_API -> toString(tc, parameterDef);
		default -> "N/A";
		};
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
		final var testCase = testCaseProperty.getValue();

		switch (column) {
		case BREAKPOINT:
			if ((boolean) value) {
				testControl.addBreakpoint(testStep);
			} else {
				testControl.removeBreakpoint(testStep);
			}
			break;
		case TO_VAR:
			testCase.publishReference(testStep.getReference().rename((String) value, "TODO"));
			break;
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
