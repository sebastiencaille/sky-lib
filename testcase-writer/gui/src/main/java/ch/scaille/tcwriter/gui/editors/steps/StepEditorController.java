package ch.scaille.tcwriter.gui.editors.steps;

import static ch.scaille.gui.mvc.GuiModel.of;
import static ch.scaille.gui.mvc.factories.BindingDependencies.preserveOnUpdateOf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import ch.scaille.gui.mvc.GuiController;
import ch.scaille.gui.mvc.IScopedSupport;
import ch.scaille.gui.mvc.properties.ObjectProperty;
import ch.scaille.tcwriter.gui.frame.TCWriterController;
import ch.scaille.tcwriter.gui.frame.TCWriterModel;
import ch.scaille.tcwriter.model.ModelUtils;
import ch.scaille.tcwriter.model.NamedObject;
import ch.scaille.tcwriter.model.testapi.TestDictionary;
import ch.scaille.tcwriter.model.testapi.TestParameterFactory;
import ch.scaille.tcwriter.model.testcase.TestParameterValue;
import ch.scaille.tcwriter.model.testcase.TestStep;

public class StepEditorController extends GuiController {

	private final StepEditorModel model;

	private final IScopedSupport changeSupport;

	private final TCWriterModel guiModel;

	public StepEditorController(final TCWriterController controller) {
		this.model = new StepEditorModel(of(controller));
		this.changeSupport = controller.getScopedChangeSupport();
		this.guiModel = controller.getModel();
	}

	public TCWriterModel getGuiModel() {
		return guiModel;
	}

	public void load() {
		final var dictionary = guiModel.getTestDictionary();
		final var testStep = guiModel.getSelectedStep();

		model.getPossibleActors().setValue(this, sorted(dictionary.getActors().values()));
		model.getActor().listenActive(actor -> {
			if (actor != null) {
				model.getPossibleActions().setValue(this, sorted(actor.getRole().getActions()));
			} else {
				model.getPossibleActions().setValue(this, Collections.emptyList());
			}
		});

		testStep.listenActive(step -> {
			if (step == null) {
				emptySelectors();
				emptyParam0();
				changeSupport.attachAll();
				return;
			}
			model.getActor().setValue(this, step.getActor());
			model.getAction().setValue(this, step.getAction());
			updateActionParameters(dictionary, testStep);
		});
		model.getAction().listenActive(action -> updateActionParameters(dictionary, testStep));

		model.getSelectorValue().setValue(this, TestParameterValue.NO_VALUE);
		model.getActionParameterValue().setValue(this, TestParameterValue.NO_VALUE);
		// set temporary cloned object, so it's possible to edit and cancel edition
		model.getSelector()
				.listenActive(selector -> model.getSelectorValue().setValue(this,
						model.getSelectorValue().getValue().derivate(selector)))
				.addDependency(preserveOnUpdateOf(testStep));

		model.getActionParameter()
				.listenActive(param -> model.getActionParameterValue().setValue(this,
						model.getActionParameterValue().getValue().derivate(param)))
				.addDependency(preserveOnUpdateOf(testStep));

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
		final var step = testStep.getValue();
		final var action = model.getAction().getValue();
		if (step == null || action == null) {
			return;
		}
		final var actionUtils = ModelUtils.actionUtils(td, action);
		actionUtils.synchronizeStep(testStep.getValue());
		if (actionUtils.hasSelector()) {
			final var selectorValue = step.getParametersValue(actionUtils.selectorIndex());
			model.getPossibleSelectors().setValue(this, sorted(td.getParameterFactories(actionUtils.selector())));
			model.getSelector().setValue(this, selectorValue.getValueFactory());
			model.getSelectorValue().setValue(this, selectorValue.derivate(selectorValue.getValueFactory()));
		} else {
			emptySelectors();
		}
		if (actionUtils.hasActionParameter(0)) {
			final var param0Value = step.getParametersValue(actionUtils.parameterIndex(0));
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
		final var sorted = new ArrayList<>(original);
		sorted.sort((n1, n2) -> n1.getName().compareTo(n2.getName()));
		return sorted;
	}

	public void applyChanges() {
		final var step = guiModel.getSelectedStep().getValue();
		step.setActor(model.getActor().getValue());
		step.setAction(model.getAction().getValue());
		step.getParametersValue().clear();
		final var actionUtils = ModelUtils.actionUtils(guiModel.getTestDictionary(), step.getAction());
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
