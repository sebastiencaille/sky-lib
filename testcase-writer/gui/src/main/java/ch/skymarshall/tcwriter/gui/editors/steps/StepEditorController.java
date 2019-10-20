package ch.skymarshall.tcwriter.gui.editors.steps;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import ch.skymarshall.gui.mvc.GuiController;
import ch.skymarshall.gui.mvc.properties.ObjectProperty;
import ch.skymarshall.tcwriter.generators.model.ModelUtils;
import ch.skymarshall.tcwriter.generators.model.ModelUtils.ActionUtils;
import ch.skymarshall.tcwriter.generators.model.NamedObject;
import ch.skymarshall.tcwriter.generators.model.testapi.TestModel;
import ch.skymarshall.tcwriter.generators.model.testapi.TestParameterDefinition;
import ch.skymarshall.tcwriter.generators.model.testcase.TestParameterValue;
import ch.skymarshall.tcwriter.generators.model.testcase.TestStep;

public class StepEditorController extends GuiController {

	private final StepEditorModel model = new StepEditorModel(this);
	private final TestModel tm;
	private final ObjectProperty<TestStep> testStep;

	public StepEditorController(final TestModel tm, final ObjectProperty<TestStep> testStep) {
		this.tm = tm;
		this.testStep = testStep;
	}

	public void init() {
		testStep.addListener(l -> {
			if (testStep.getValue() == null) {
				return;
			}
			model.getActor().setValue(this, testStep.getValue().getActor());
			model.getAction().setValue(this, testStep.getValue().getAction());
		});

		model.getActor().addListener(l -> model.getPossibleActions().setValue(this,
				sorted(model.getActor().getValue().getRole().getActions())));

		model.getAction().addListener(l -> {
			if (model.getAction().getValue() == null) {
				model.getPossibleSelectors().setValue(this, Collections.emptyList());
				model.getPossibleActionParameters().setValue(this, Collections.emptyList());
				return;
			}
			final ActionUtils actionUtils = ModelUtils.actionUtils(tm, model.getAction().getValue());
			if (actionUtils.hasSelector()) {
				final TestParameterValue selectorValue = testStep.getValue()
						.getParametersValue(actionUtils.selectorIndex());
				model.getPossibleSelectors().setValue(this,
						sorted(tm.getParameterFactories(actionUtils.selectorType())));
				model.getSelector().setValue(this, selectorValue.getValueDefinition());
				model.getSelectorValue().setValue(this,
						createParameter(selectorValue, selectorValue.getValueDefinition()));
			} else {
				model.getPossibleSelectors().setValue(this, Collections.emptyList());
			}
			if (actionUtils.hasActionParameter(0)) {
				final TestParameterValue param0Value = testStep.getValue()
						.getParametersValue(actionUtils.actionParameterIndex(0));
				model.getPossibleActionParameters().setValue(this,
						sorted(tm.getParameterFactories(actionUtils.parameterType(0))));
				model.getActionParameter().setValue(this, param0Value.getValueDefinition());
				model.getActionParameterValue().setValue(this,
						createParameter(param0Value, param0Value.getValueDefinition()));
			} else {
				model.getPossibleActionParameters().setValue(this, Collections.emptyList());
			}

		});
		model.getSelector().addListener(l -> {
			model.getSelectorValue().setValue(this,
					createParameter(model.getSelectorValue().getValue(), model.getSelector().getValue()));
		});
		model.getActionParameter().addListener(l -> {
			model.getActionParameterValue().setValue(this,
					createParameter(model.getActionParameterValue().getValue(), model.getActionParameter().getValue()));
		});
		model.getPossibleActors().setValue(this, sorted(tm.getActors().values()));
		model.getSelectorValue().setValue(this, new TestParameterValue("", TestParameterDefinition.NO_PARAMETER));
		model.getActionParameterValue().setValue(this,
				new TestParameterValue("", TestParameterDefinition.NO_PARAMETER));
	}

	public TestParameterValue createParameter(final TestParameterValue oldValue,
			final TestParameterDefinition newDefinition) {
		TestParameterDefinition definition = newDefinition;
		if (definition == null) {
			definition = TestParameterDefinition.NO_PARAMETER;
		}
		final TestParameterValue newValue = new TestParameterValue(UUID.randomUUID().toString(),
				oldValue.getApiParameterId(), definition, oldValue.getSimpleValue());
		newValue.getComplexTypeValues().putAll(oldValue.getComplexTypeValues());
		return newValue;
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
