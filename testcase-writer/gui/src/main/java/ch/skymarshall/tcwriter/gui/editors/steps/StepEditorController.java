package ch.skymarshall.tcwriter.gui.editors.steps;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import javax.swing.JFrame;

import ch.skymarshall.gui.mvc.GuiController;
import ch.skymarshall.tcwriter.generators.model.ModelUtils;
import ch.skymarshall.tcwriter.generators.model.ModelUtils.ActionUtils;
import ch.skymarshall.tcwriter.generators.model.NamedObject;
import ch.skymarshall.tcwriter.generators.model.testapi.TestModel;
import ch.skymarshall.tcwriter.generators.model.testapi.TestParameterDefinition;
import ch.skymarshall.tcwriter.generators.model.testcase.TestCase;
import ch.skymarshall.tcwriter.generators.model.testcase.TestParameterValue;
import ch.skymarshall.tcwriter.generators.model.testcase.TestStep;

public class StepEditorController extends GuiController {

	private final StepEditorModel model = new StepEditorModel(this);
	private final StepEditorFrame frame;
	private final TestCase tc;
	private final TestModel testModel;
	private final TestStep testStep;

	public StepEditorController(final TestCase tc, final TestStep testStep) {
		this.tc = tc;
		this.testStep = testStep;
		frame = new StepEditorFrame(this, tc);
		testModel = tc.getModel();
		model.getActor().addListener(l -> model.getPossibleActions().setValue(this,
				sorted(model.getActor().getValue().getRole().getActions())));

		model.getAction().addListener(l -> {
			if (model.getAction().getValue() == null) {
				model.getPossibleSelectors().setValue(this, Collections.emptyList());
				model.getPossibleActionParameters().setValue(this, Collections.emptyList());
				return;
			}
			final ActionUtils actionUtils = ModelUtils.actionUtils(testModel, model.getAction().getValue());
			if (actionUtils.hasSelector()) {
				final TestParameterValue selector = testStep.getParametersValue(actionUtils.selectorIndex());
				model.getPossibleSelectors().setValue(this,
						sorted(testModel.getParameterFactories(actionUtils.selectorType())));
				model.getSelector().setValue(this, selector.getValueDefinition());
				model.getSelectorValue().setValue(this, selector);
			} else {
				model.getPossibleSelectors().setValue(this, Collections.emptyList());
			}
			if (actionUtils.hasActionParameter(0)) {
				final TestParameterValue param0 = testStep.getParametersValue(actionUtils.actionParameterIndex(0));
				model.getPossibleActionParameters().setValue(this,
						sorted(testModel.getParameterFactories(actionUtils.parameterType(0))));
				model.getActionParameter().setValue(this, param0.getValueDefinition());
				model.getActionParameterValue().setValue(this, param0);
			} else {
				model.getPossibleActionParameters().setValue(this, Collections.emptyList());
			}

		});
		model.getSelector().addListener(l -> {
			TestParameterDefinition selectorDefinition;
			if (model.getActionParameter().getValue() != null) {
				selectorDefinition = model.getSelector().getValue();
			} else {
				selectorDefinition = TestParameterDefinition.NO_PARAMETER;
			}
			final TestParameterValue oldSelector = model.getSelectorValue().getValue();
			final TestParameterValue newSelector = new TestParameterValue(UUID.randomUUID().toString(),
					oldSelector.getApiParameterId(), selectorDefinition, oldSelector.getSimpleValue());
			newSelector.getComplexTypeValues().putAll(oldSelector.getComplexTypeValues());
			model.getSelectorValue().setValue(this, newSelector);
		});
		model.getActionParameter().addListener(l -> {
			TestParameterDefinition paramDefinition;
			if (model.getActionParameter().getValue() != null) {
				paramDefinition = model.getActionParameter().getValue();
			} else {
				paramDefinition = TestParameterDefinition.NO_PARAMETER;
			}
			final TestParameterValue oldParam0 = model.getActionParameterValue().getValue();
			final TestParameterValue newParam0 = new TestParameterValue(UUID.randomUUID().toString(),
					oldParam0.getApiParameterId(), paramDefinition, oldParam0.getSimpleValue());
			newParam0.getComplexTypeValues().putAll(oldParam0.getComplexTypeValues());
			model.getActionParameterValue().setValue(this, newParam0);
		});
		model.getSelectorValue().setValue(this, new TestParameterValue("", TestParameterDefinition.NO_PARAMETER));
		model.getActionParameterValue().setValue(this,
				new TestParameterValue("", TestParameterDefinition.NO_PARAMETER));
		activate();
	}

	@Override
	public void activate() {
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
	}

	public StepEditorModel getModel() {
		return model;
	}

	public <T extends NamedObject> List<T> sorted(final Collection<T> original) {
		final List<T> sorted = new ArrayList<>(original);
		sorted.sort((n1, n2) -> n1.getName().compareTo(n2.getName()));
		return sorted;
	}

	public void load() {
		model.getPossibleActors().setValue(this, new ArrayList<>(sorted(testModel.getActors().values())));

		model.getActor().setValue(this, testStep.getActor());
		model.getAction().setValue(this, testStep.getAction());
	}

}
