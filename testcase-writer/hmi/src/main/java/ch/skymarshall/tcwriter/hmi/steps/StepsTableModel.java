package ch.skymarshall.tcwriter.hmi.steps;

import java.util.List;

import org.skymarshall.hmi.model.ListModel;
import org.skymarshall.hmi.swing.model.ListModelTableModel;

import ch.skymarshall.tcwriter.generators.Helper.Reference;
import ch.skymarshall.tcwriter.generators.model.IdObject;
import ch.skymarshall.tcwriter.generators.model.TestAction;
import ch.skymarshall.tcwriter.generators.model.TestCase;
import ch.skymarshall.tcwriter.generators.model.TestParameter;
import ch.skymarshall.tcwriter.generators.model.TestParameter.ParameterNature;
import ch.skymarshall.tcwriter.generators.model.TestParameterType;
import ch.skymarshall.tcwriter.generators.model.TestParameterValue;
import ch.skymarshall.tcwriter.generators.model.TestStep;
import ch.skymarshall.tcwriter.hmi.TestControl;

public class StepsTableModel extends ListModelTableModel<TestStep, StepsTableModel.Column> {

	private final ListModel<TestStep> steps;
	private final TestCase tc;
	private final TestControl testControl;

	public enum Column {
		BREAKPOINT, STEP, ACTOR, METHOD, NAVIGATOR, PARAM0, TO_VALUE
	}

	public StepsTableModel(final ListModel<TestStep> steps, final TestCase tc, final TestControl testControl) {
		super(steps, Column.class);
		this.steps = steps;
		this.tc = tc;
		this.testControl = testControl;
	}

	@Override
	protected Object getValueAtColumn(final TestStep testStep, final Column column) {
		IdObject tcObject;
		final ParameterNature nature = ParameterNature.TEST_API_TYPE;
		final List<TestParameterValue> parametersValue = testStep.getParametersValue();
		switch (column) {
		case BREAKPOINT:
			return testControl.hasBreakpoint(testStep);
		case STEP:
			return testStep.getOrdinal();
		case ACTOR:
			tcObject = testStep.getActor();
			break;
		case METHOD:
			tcObject = testStep.getAction();
			break;
		case NAVIGATOR:
			if (!hasNavigationParam(testStep)) {
				return "";
			}
			return createReferenceFromParam(parametersValue.get(0));
		case PARAM0:
			if (hasParam(testStep, 0)) {
				return createReferenceFromParam(parametersValue.get(paramIndexOf(testStep, 0)));
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

	private Object createReferenceFromParam(final TestParameterValue parameterValue) {
		final TestParameter parameterDef = parameterValue.getTestParameter();
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
		final TestStep testStep = steps.getValueAt(rowIndex);
		final Column column = columnOf(columnIndex);
		switch (column) {
		case BREAKPOINT:
		case ACTOR:
		case METHOD:
			return true;
		case NAVIGATOR:
			return hasNavigationParam(testStep);
		case PARAM0:
			return hasParam(testStep, 0);
		case TO_VALUE:
			return true;
		default:
			return false;
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
			return new TestParameterValue(reference.getId(), tc.getModel().getTestParameterFactory(reference.getId()));
		default:
			return TestParameterValue.NO_VALUE;
		}

	}

	@Override
	public String getColumnName(final int column) {
		return Column.values()[column].name();
	}

	@Override
	protected void setValueAtColumn(final TestStep testStep, final Column column, final Object value) {
		if (value == null) {
			return;
		}

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
		}

		final Reference reference = (Reference) value;
		final String newId = reference.getId();
		switch (column) {
		case ACTOR:
			testStep.setActor(tc.getModel().getActors().get(newId));
			testStep.setAction(TestAction.NOT_SET);
			testStep.getParametersValue().clear();
			return;
		case METHOD:
			final TestAction oldMethod = testStep.getAction();
			testStep.setAction(testStep.getRole().getApi(newId));

			for (int i = 0; i < testStep.getParametersValue().size(); i++) {
				final TestParameterValue newMethodParameter = testStep.getParametersValue().get(i);
				if (i >= oldMethod.getParameters().size()) {
					testStep.addParameter(TestParameterValue.NO_VALUE);
				} else if (newMethodParameter.getTestParameter().getType()
						.equals(oldMethod.getParameters().get(i).getType())) {
					testStep.getParametersValue().set(i, TestParameterValue.NO_VALUE);
				}
			}
			for (int i = testStep.getParametersValue().size(); i < testStep.getAction().getParameters().size(); i++) {
				testStep.getParametersValue().add(TestParameterValue.NO_VALUE);
			}
			return;
		case NAVIGATOR:
			if (hasNavigationParam(testStep)) {
				testStep.getParametersValue().set(0,
						createParameterValue(reference, testStep.getAction().getParameter(0)));
			}
			return;
		case PARAM0:
			if (hasParam(testStep, 0)) {
				final int paramIndex = paramIndexOf(testStep, 0);
				testStep.getParametersValue().set(paramIndex,
						createParameterValue(reference, testStep.getAction().getParameter(paramIndex)));
			}
			return;
		default:

		}
	}

	int paramIndexOf(final TestStep testStep, final int index) {
		int paramIndex;
		if (hasNavigationParam(testStep)) {
			paramIndex = 1;
		} else {
			paramIndex = 0;
		}
		return paramIndex + index;
	}

	boolean hasNavigationParam(final TestStep testStep) {
		return !testStep.getAction().getParameters().isEmpty()
				&& tc.getModel().isNavigation(testStep.getAction().getParameter(0));
	}

	boolean hasParam(final TestStep testStep, final int index) {
		return paramIndexOf(testStep, index) < testStep.getAction().getParameters().size();
	}

}
