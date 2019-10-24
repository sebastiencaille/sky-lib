package ch.skymarshall.tcwriter.gui.editors.steps;

import java.util.List;

import ch.skymarshall.gui.mvc.ControllerPropertyChangeSupport;
import ch.skymarshall.gui.mvc.GuiModel;
import ch.skymarshall.gui.mvc.properties.ObjectProperty;
import ch.skymarshall.tcwriter.generators.model.testapi.TestAction;
import ch.skymarshall.tcwriter.generators.model.testapi.TestActor;
import ch.skymarshall.tcwriter.generators.model.testapi.TestParameterDefinition;
import ch.skymarshall.tcwriter.generators.model.testcase.TestParameterValue;

public class StepEditorModel extends GuiModel {

	private final ObjectProperty<List<TestActor>> possibleActors = new ObjectProperty<>("possibleActors",
			propertySupport);
	private final ObjectProperty<TestActor> actor = new ObjectProperty<>("actor", propertySupport);
	private final ObjectProperty<List<TestAction>> possibleActions = new ObjectProperty<>("possibleActions",
			propertySupport);
	private final ObjectProperty<TestAction> action = new ObjectProperty<>("action", propertySupport);
	private final ObjectProperty<List<TestParameterDefinition>> possibleSelectors = new ObjectProperty<>(
			"possibleSelectors", propertySupport);
	private final ObjectProperty<TestParameterDefinition> selector = new ObjectProperty<>("selector", propertySupport);
	private final ObjectProperty<TestParameterValue> selectorValues = new ObjectProperty<>("selectorValues",
			propertySupport);
	private final ObjectProperty<List<TestParameterDefinition>> possibleActionParameters = new ObjectProperty<>(
			"possibleActionParameters", propertySupport);
	private final ObjectProperty<TestParameterDefinition> actionParameter = new ObjectProperty<>("actionParameter",
			propertySupport);
	private final ObjectProperty<TestParameterValue> actionParameterValues = new ObjectProperty<>(
			"actionParameterValues", propertySupport);

	public StepEditorModel(final ControllerPropertyChangeSupport support) {
		super(support);
	}

	public ObjectProperty<List<TestActor>> getPossibleActors() {
		return possibleActors;
	}

	public ObjectProperty<TestActor> getActor() {
		return actor;
	}

	public ObjectProperty<List<TestAction>> getPossibleActions() {
		return possibleActions;
	}

	public ObjectProperty<TestAction> getAction() {
		return action;
	}

	public ObjectProperty<List<TestParameterDefinition>> getPossibleSelectors() {
		return possibleSelectors;
	}

	public ObjectProperty<TestParameterDefinition> getSelector() {
		return selector;
	}

	public ObjectProperty<TestParameterValue> getSelectorValue() {
		return selectorValues;
	}

	public ObjectProperty<List<TestParameterDefinition>> getPossibleActionParameters() {
		return possibleActionParameters;
	}

	public ObjectProperty<TestParameterDefinition> getActionParameter() {
		return actionParameter;
	}

	public ObjectProperty<TestParameterValue> getActionParameterValue() {
		return actionParameterValues;
	}
}