package ch.skymarshall.tcwriter.hmi.steps;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import javax.swing.event.TableModelEvent;

import org.skymarshall.hmi.model.ListModel;
import org.skymarshall.hmi.mvc.properties.ObjectProperty;
import org.skymarshall.hmi.swing.model.ListModelTableModel;

import ch.skymarshall.tcwriter.generators.Helper.Reference;
import ch.skymarshall.tcwriter.generators.model.IdObject;
import ch.skymarshall.tcwriter.generators.model.testapi.TestAction;
import ch.skymarshall.tcwriter.generators.model.testapi.TestParameter;
import ch.skymarshall.tcwriter.generators.model.testapi.TestParameter.ParameterNature;
import ch.skymarshall.tcwriter.generators.model.testcase.TestCase;
import ch.skymarshall.tcwriter.generators.model.testcase.TestParameterValue;
import ch.skymarshall.tcwriter.generators.model.testcase.TestStep;
import ch.skymarshall.tcwriter.generators.visitors.HumanReadableVisitor;
import ch.skymarshall.tcwriter.hmi.TestRemoteControl;
import ch.skymarshall.tcwriter.hmi.steps.StepsCellEditor.EditorValue;

public class StepsTableModel extends ListModelTableModel<TestStep, StepsTableModel.Column> {

	private final ListModel<TestStep> steps;
	private final TestRemoteControl testControl;
	private final ObjectProperty<TestCase> testCaseProperty;

	public enum Column {
		BREAKPOINT, STEP, ACTOR, ACTION, NAVIGATOR, PARAM0, TO_VALUE
	}

	private HumanReadableVisitor summaryVisitor;

	public StepsTableModel(final ObjectProperty<TestCase> testCaseProperty, final ListModel<TestStep> steps,
			final TestRemoteControl testControl) {
		super(steps, Column.class);
		this.testCaseProperty = testCaseProperty;
		this.steps = steps;
		this.testControl = testControl;
		testCaseProperty.addListener(new PropertyChangeListener() {

			@Override
			public void propertyChange(final PropertyChangeEvent evt) {
				summaryVisitor = new HumanReadableVisitor(testCaseProperty.getObjectValue());
			}
		});
	}

	@Override
	protected Object getValueAtColumn(final TestStep testStep, final Column column) {
		final TestCase tc = testCaseProperty.getValue();

		IdObject tcObject;
		final ParameterNature nature = ParameterNature.TEST_API_TYPE;
		final List<TestParameterValue> parametersValue = testStep.getParametersValue();
		switch (column) {
		case BREAKPOINT:
			return testControl.stepStatus(testStep.getOrdinal());
		case STEP:
			return testStep.getOrdinal();
		case ACTOR:
			tcObject = testStep.getActor();
			break;
		case ACTION:
			tcObject = testStep.getAction();
			break;
		case NAVIGATOR:
			if (!hasNavigationParam(tc, testStep)) {
				return "";
			}
			return createReferenceFromParam(tc, parametersValue.get(0));
		case PARAM0:
			if (hasParam(tc, testStep, 0)) {
				return createReferenceFromParam(tc, parametersValue.get(paramIndexOf(tc, testStep, 0)));
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
		return new Reference(tcObject.getId(), tc.descriptionOf(tcObject).getDescription(), nature);
	}

	private Object createReferenceFromParam(final TestCase tc, final TestParameterValue parameterValue) {
		final TestParameter parameterDef = parameterValue.getValueDefinition();
		String display;
		switch (parameterDef.getNature()) {
		case REFERENCE:
			display = tc.descriptionOf(parameterValue.getSimpleValue()).getDescription();
			break;
		case SIMPLE_TYPE:
			display = parameterValue.getSimpleValue();
			break;
		case TEST_API_TYPE:
			display = tc.descriptionOf(parameterDef).getDescription();
			break;
		default:
			display = "N/A";
		}
		return new Reference(parameterDef.getId(), display, parameterDef.getNature());
	}

	@Override
	public boolean isCellEditable(final int rowIndex, final int columnIndex) {
		if (rowIndex % 2 == 0) {
			return false;
		}
		final TestCase tc = testCaseProperty.getValue();
		final TestStep testStep = getObjectAtRow(rowIndex);
		final Column column = columnOf(columnIndex);
		switch (column) {
		case BREAKPOINT:
		case ACTOR:
		case ACTION:
			return true;
		case NAVIGATOR:
			return hasNavigationParam(tc, testStep);
		case PARAM0:
			return hasParam(tc, testStep, 0);
		case TO_VALUE:
			return true;
		default:
			return false;
		}
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

		final Reference reference;
		if (value instanceof EditorValue) {
			reference = ((EditorValue) value).testFactoryReference;
		} else {
			reference = (Reference) value;
		}
		final String newId = reference.getId();
		switch (column) {
		case ACTOR:
			testStep.setActor(tc.getModel().getActors().get(newId));
			testStep.setAction(TestAction.NOT_SET);
			testStep.getParametersValue().clear();
			return;
		case ACTION:
			final TestAction oldAction = testStep.getAction();
			testStep.setAction(testStep.getRole().getApi(newId));
			migrateOldAction(testStep, oldAction);
			return;
		case NAVIGATOR:
			if (!hasNavigationParam(tc, testStep)) {
				return;
			}
			testStep.getParametersValue().set(0, ((EditorValue) value).factorParameterValue);
			return;
		case PARAM0:
			if (!hasParam(tc, testStep, 0)) {
				return;
			}
			final int paramIndex = paramIndexOf(tc, testStep, 0);
			final TestParameter testParameter = testStep.getParametersValue().get(paramIndex).getValueDefinition();
			switch (testParameter.getNature()) {
			case SIMPLE_TYPE:
				testStep.getParametersValue().set(paramIndex,
						new TestParameterValue(testStep.getAction().getParameter(paramIndex).getId(), testParameter,
								((Reference) value).getDisplay()));
				break;
			case REFERENCE:
				testStep.getParametersValue().set(paramIndex,
						new TestParameterValue(testStep.getAction().getParameter(paramIndex).getId(), testParameter,
								((Reference) value).getDisplay()));
				break;
			case TEST_API_TYPE:
				testStep.getParametersValue().set(paramIndex, ((EditorValue) value).factorParameterValue);
				break;
			}
			return;
		default:
		}
	}

	private void migrateOldAction(final TestStep testStep, final TestAction oldAction) {
		for (int i = 0; i < testStep.getParametersValue().size(); i++) {
			final TestParameterValue newMethodParameter = testStep.getParametersValue().get(i);
			if (i >= oldAction.getParameters().size()) {
				testStep.addParameter(TestParameterValue.NO_VALUE);
			} else if (newMethodParameter.getValueDefinition().getType()
					.equals(oldAction.getParameters().get(i).getType())) {
				testStep.getParametersValue().set(i, TestParameterValue.NO_VALUE);
			}
		}
		for (int i = testStep.getParametersValue().size(); i < testStep.getAction().getParameters().size(); i++) {
			testStep.getParametersValue().add(TestParameterValue.NO_VALUE);
		}
	}

	int paramIndexOf(final TestCase tc, final TestStep testStep, final int index) {
		int paramIndex;
		if (hasNavigationParam(tc, testStep)) {
			paramIndex = 1;
		} else {
			paramIndex = 0;
		}
		return paramIndex + index;
	}

	boolean hasNavigationParam(final TestCase tc, final TestStep testStep) {
		return !testStep.getAction().getParameters().isEmpty()
				&& tc.getModel().isNavigation(testStep.getAction().getParameter(0));
	}

	boolean hasParam(final TestCase tc, final TestStep testStep, final int index) {
		return paramIndexOf(tc, testStep, index) < testStep.getAction().getParameters().size();
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
