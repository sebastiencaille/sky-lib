package ch.skymarshall.tcwriter.gui.editors.steps;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import ch.skymarshall.gui.mvc.GuiController;
import ch.skymarshall.gui.mvc.properties.ObjectProperty;
import ch.skymarshall.tcwriter.generators.model.ModelUtils;
import ch.skymarshall.tcwriter.generators.model.ModelUtils.ActionUtils;
import ch.skymarshall.tcwriter.generators.model.NamedObject;
import ch.skymarshall.tcwriter.generators.model.testapi.TestModel;
import ch.skymarshall.tcwriter.generators.model.testapi.TestParameterFactory;
import ch.skymarshall.tcwriter.generators.model.testcase.TestParameterValue;
import ch.skymarshall.tcwriter.generators.model.testcase.TestStep;

public class StepEditorController extends GuiController {

	private final StepEditorModel model;

	private final TestModel tm;
	private final ObjectProperty<TestStep> testStep;

	public StepEditorController(final StepEditorModel model, final TestModel tm,
			final ObjectProperty<TestStep> testStep) {
		this.model = model;
		this.tm = tm;
		this.testStep = testStep;
	}

	public void init() {
		testStep.listen(step -> {
			if (step == null) {
				return;
			}
			model.getActor().setValue(this, step.getActor());
			model.getAction().setValue(this, step.getAction());
		});

		model.getActor()
				.listen(actor -> model.getPossibleActions().setValue(this, sorted(actor.getRole().getActions())));

		model.getAction().listen(action -> {
			if (action == null) {
				emptySelectors();
				emptyParam0();
				return;
			}
			final ActionUtils actionUtils = ModelUtils.actionUtils(tm, action);
			actionUtils.synchronizeStep(testStep.getValue());
			if (actionUtils.hasSelector()) {
				final TestParameterValue selectorValue = testStep.getValue()
						.getParametersValue(actionUtils.selectorIndex());
				model.getPossibleSelectors().setValue(this,
						sorted(tm.getParameterFactories(actionUtils.selector())));
				model.getSelector().setValue(this, selectorValue.getValueFactory());
				model.getSelectorValue().setValue(this, selectorValue.derivate(selectorValue.getValueFactory()));
			} else {
				emptySelectors();
			}
			if (actionUtils.hasActionParameter(0)) {
				final TestParameterValue param0Value = testStep.getValue()
						.getParametersValue(actionUtils.parameterIndex(0));
				model.getPossibleActionParameters().setValue(this,
						sorted(tm.getParameterFactories(actionUtils.parameter(0))));
				model.getActionParameter().setValue(this, param0Value.getValueFactory());
				model.getActionParameterValue().setValue(this, param0Value.derivate(param0Value.getValueFactory()));
			} else {
				emptyParam0();
			}

		});

		model.getSelector().listen(selector -> model.getSelectorValue().setValue(this,
				model.getSelectorValue().getValue().derivate(selector)));

		model.getActionParameter().listen(param -> model.getActionParameterValue().setValue(this,
				model.getActionParameterValue().getValue().derivate(param)));

		model.getPossibleActors().setValue(this, sorted(tm.getActors().values()));

		model.getSelectorValue().setValue(this, new TestParameterValue("", TestParameterFactory.NO_FACTORY));
		model.getActionParameterValue().setValue(this, new TestParameterValue("", TestParameterFactory.NO_FACTORY));
	}

	private void emptyParam0() {
		model.getActionParameterValue().setValue(this, TestParameterValue.NO_VALUE);
		model.getPossibleActionParameters().setValue(this, Collections.emptyList());
	}

	private void emptySelectors() {
		model.getSelectorValue().setValue(this, TestParameterValue.NO_VALUE);
		model.getPossibleSelectors().setValue(this, Collections.emptyList());
	}

	public StepEditorModel getModel() {
		return model;
	}

	public static <T extends NamedObject> List<T> sorted(final Collection<T> original) {
		final List<T> sorted = new ArrayList<>(original);
		sorted.sort((n1, n2) -> n1.getName().compareTo(n2.getName()));
		return sorted;
	}

	public void applyChanges() {
		final TestStep step = testStep.getValue();
		step.setActor(model.getActor().getValue());
		step.setAction(model.getAction().getValue());
		step.getParametersValue().clear();
		final ActionUtils actionUtils = ModelUtils.actionUtils(tm, step.getAction());
		step.getParametersValue().clear();
		if (actionUtils.hasSelector()) {
			step.getParametersValue().add(model.getSelectorValue().getValue());
		}
		if (actionUtils.hasActionParameter(0)) {
			step.getParametersValue().add(model.getActionParameterValue().getValue());
		}
		testStep.forceChanged(this);
	}

	public void cancelChanges() {
		getModel().getActor().forceChanged(this);
		getModel().getAction().forceChanged(this);
	}

}
