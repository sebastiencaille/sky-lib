package ch.scaille.tcwriter.gui.editors.steps;

import static ch.scaille.gui.mvc.GuiModel.of;
import static ch.scaille.javabeans.BindingDependencies.preserveOnUpdateOf;

import java.util.*;

import ch.scaille.gui.mvc.GuiController;
import ch.scaille.javabeans.IPropertiesGroup;
import ch.scaille.javabeans.properties.ObjectProperty;
import ch.scaille.tcwriter.gui.frame.TCWriterController;
import ch.scaille.tcwriter.gui.frame.TCWriterModel;
import ch.scaille.tcwriter.model.ModelUtils;
import ch.scaille.tcwriter.model.NamedObject;
import ch.scaille.tcwriter.model.dictionary.TestDictionary;
import ch.scaille.tcwriter.model.dictionary.TestParameterFactory;
import ch.scaille.tcwriter.model.testcase.ExportableTestParameterValue;
import ch.scaille.tcwriter.model.testcase.TestStep;

public class StepEditorController extends GuiController {

	private final StepEditorModel model;

	private final IPropertiesGroup changeSupport;

	private final TCWriterModel guiModel;

	public StepEditorController(final TCWriterController controller, ObjectProperty<TestDictionary> testDictionary) {
		this.model = new StepEditorModel(of(controller), testDictionary);
		this.changeSupport = controller.getScopedChangeSupport();
		this.guiModel = controller.getModel();
	}

	public TCWriterModel getGuiModel() {
		return guiModel;
	}

	public void build() {
		final var dictionary = guiModel.getTestDictionary();
		final var testStep = guiModel.getSelectedStep();

		dictionary.listen(dict -> model.getPossibleActors().setValue(this, sorted(dict.getActors().values())));
		
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
			updateActionParameters(dictionary.getValue(), testStep);
		});
		model.getAction().listenActive(action -> updateActionParameters(dictionary.getValue(), testStep));

		model.getSelectorValue().setValue(this, ExportableTestParameterValue.NO_VALUE);
		model.getActionParameterValue().setValue(this, ExportableTestParameterValue.NO_VALUE);
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
		model.getActionParameterValue().setValue(this, ExportableTestParameterValue.NO_VALUE);
		model.getActionParameter().setValue(this, TestParameterFactory.NO_FACTORY);
		model.getPossibleActionParameters().setValue(this, Collections.emptyList());
	}

	private void emptySelectors() {
		model.getSelectorValue().setValue(this, ExportableTestParameterValue.NO_VALUE);
		model.getSelector().setValue(this, TestParameterFactory.NO_FACTORY);
		model.getPossibleSelectors().setValue(this, Collections.emptyList());
	}

	public StepEditorModel getModel() {
		return model;
	}

	public static <T extends NamedObject> List<T> sorted(final Collection<T> original) {
		final var sorted = new ArrayList<>(original);
		sorted.sort(Comparator.comparing(NamedObject::getName));
		return sorted;
	}

	public void applyChanges() {
		final var step = guiModel.getSelectedStep().getValue();
		step.setActor(model.getActor().getValue());
		step.setAction(model.getAction().getValue());
		step.getParametersValue().clear();
		
		final var actionUtils = guiModel.getTestDictionary().map(dictionary ->  ModelUtils.actionUtils(dictionary, step.getAction()));
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
