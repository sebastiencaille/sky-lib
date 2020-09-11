package ch.skymarshall.tcwriter.gui.editors.steps;

import static ch.skymarshall.gui.mvc.factories.BindingDependencies.detachOnUpdateOf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import ch.skymarshall.gui.mvc.GuiController;
import ch.skymarshall.gui.mvc.IScopedSupport;
import ch.skymarshall.gui.mvc.properties.ObjectProperty;
import ch.skymarshall.tcwriter.generators.model.ModelUtils;
import ch.skymarshall.tcwriter.generators.model.ModelUtils.ActionUtils;
import ch.skymarshall.tcwriter.generators.model.NamedObject;
import ch.skymarshall.tcwriter.generators.model.testapi.TestAction;
import ch.skymarshall.tcwriter.generators.model.testapi.TestDictionary;
import ch.skymarshall.tcwriter.generators.model.testapi.TestParameterFactory;
import ch.skymarshall.tcwriter.generators.model.testcase.TestParameterValue;
import ch.skymarshall.tcwriter.generators.model.testcase.TestStep;
import ch.skymarshall.tcwriter.gui.frame.TCWriterController;
import ch.skymarshall.tcwriter.gui.frame.TCWriterModel;

public class StepEditorController extends GuiController {

	private final StepEditorModel model;

	private final TCWriterModel guiModel;

	private final IScopedSupport changeSupport;

	public StepEditorController(final TCWriterController controller) {
		changeSupport = controller.getPropertyChangeSupport();
		this.model = new StepEditorModel(changeSupport);
		guiModel = controller.getModel();
	}

	public TCWriterModel getGuiModel() {
		return guiModel;
	}

	public void load() {
		final TestDictionary td = guiModel.getTestDictionary();
		final ObjectProperty<TestStep> testStep = guiModel.getSelectedStep();

		model.getPossibleActors().setValue(this, sorted(td.getActors().values()));
		model.getActor().listen(actor -> {
			if (actor != null) {
				model.getPossibleActions().setValue(this, sorted(actor.getRole().getActions()));
			} else {
				model.getPossibleActions().setValue(this, Collections.emptyList());
			}
		});

		testStep.listen(step -> {
			if (step == null) {
				emptySelectors();
				emptyParam0();
				changeSupport.attachAll();
				return;
			}
			model.getActor().setValue(this, step.getActor());
			model.getAction().setValue(this, step.getAction());
			updateActionParameters(td, testStep);
		});
		model.getAction().listen(action -> updateActionParameters(td, testStep));

		model.getSelectorValue().setValue(this, TestParameterValue.NO_VALUE);
		model.getActionParameterValue().setValue(this, TestParameterValue.NO_VALUE);
		// set temporary cloned object, so it's possible to edit and cancel edition
		model.getSelector()
				.listen(selector -> model.getSelectorValue().setValue(this,
						model.getSelectorValue().getValue().derivate(selector)))
				.addDependency(detachOnUpdateOf(testStep));

		model.getActionParameter()
				.listen(param -> model.getActionParameterValue().setValue(this,
						model.getActionParameterValue().getValue().derivate(param)))
				.addDependency(detachOnUpdateOf(testStep));

	}

	/**
	 * Called when changing the step (because possibly same action but step has
	 * different action parameters) or when changing the action (because same step
	 * but action has different parameters)
	 *
	 * @param td
	 * @param testStep
	 */
	private void updateActionParameters(final TestDictionary td, final ObjectProperty<TestStep> testStep) {
		final TestStep step = testStep.getValue();
		final TestAction action = model.getAction().getValue();
		if (step == null || action == null) {
			return;
		}
		final ActionUtils actionUtils = ModelUtils.actionUtils(td, action);
		actionUtils.synchronizeStep(testStep.getValue());
		if (actionUtils.hasSelector()) {
			final TestParameterValue selectorValue = step.getParametersValue(actionUtils.selectorIndex());
			model.getPossibleSelectors().setValue(this, sorted(td.getParameterFactories(actionUtils.selector())));
			model.getSelector().setValue(this, selectorValue.getValueFactory());
			model.getSelectorValue().setValue(this, selectorValue.derivate(selectorValue.getValueFactory()));
		} else {
			emptySelectors();
		}
		if (actionUtils.hasActionParameter(0)) {
			final TestParameterValue param0Value = step.getParametersValue(actionUtils.parameterIndex(0));
			model.getPossibleActionParameters().setValue(this,
					sorted(td.getParameterFactories(actionUtils.parameter(0))));
			model.getActionParameter().setValue(this, param0Value.getValueFactory());
			model.getActionParameterValue().setValue(this, param0Value.derivate(param0Value.getValueFactory()));
		} else {
			emptyParam0();
		}
	}

	private void emptyParam0() {
		model.getActionParameterValue().setValue(this, TestParameterValue.NO_VALUE);
		model.getActionParameter().setValue(this, TestParameterFactory.NO_FACTORY);
		model.getPossibleActionParameters().setValue(this, Collections.emptyList());
	}

	private void emptySelectors() {
		model.getSelectorValue().setValue(this, TestParameterValue.NO_VALUE);
		model.getSelector().setValue(this, TestParameterFactory.NO_FACTORY);
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
		final TestStep step = guiModel.getSelectedStep().getValue();
		step.setActor(model.getActor().getValue());
		step.setAction(model.getAction().getValue());
		step.getParametersValue().clear();
		final ActionUtils actionUtils = ModelUtils.actionUtils(guiModel.getTestDictionary(), step.getAction());
		if (actionUtils.hasSelector()) {
			step.getParametersValue().add(model.getSelectorValue().getValue());
		}
		if (actionUtils.hasActionParameter(0)) {
			step.getParametersValue().add(model.getActionParameterValue().getValue());
		}
		guiModel.getSelectedStep().forceChanged(this);
	}

	public void cancelChanges() {
		getModel().getActor().forceChanged(this);
		getModel().getAction().forceChanged(this);
	}

}
