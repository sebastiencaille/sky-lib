package ch.scaille.tcwriter.gui.editors.steps;

import java.util.List;

import ch.scaille.gui.mvc.GuiModel;
import ch.scaille.gui.mvc.factories.ObjectTextView;
import ch.scaille.javabeans.converters.IConverter;
import ch.scaille.javabeans.properties.ObjectProperty;
import ch.scaille.tcwriter.model.NamedObject;
import ch.scaille.tcwriter.model.dictionary.StepClassifier;
import ch.scaille.tcwriter.model.dictionary.TestAction;
import ch.scaille.tcwriter.model.dictionary.TestActor;
import ch.scaille.tcwriter.model.dictionary.TestDictionary;
import ch.scaille.tcwriter.model.dictionary.TestParameterFactory;
import ch.scaille.tcwriter.model.testcase.ExportableTestParameterValue;
import ch.scaille.tcwriter.model.testcase.TestParameterValue;

public class StepEditorModel extends GuiModel {

	private final ObjectProperty<List<TestActor>> possibleActors = new ObjectProperty<>("possibleActors", this);
	private final ObjectProperty<TestActor> actor = new ObjectProperty<>("actor", this);
	private final ObjectProperty<List<TestAction>> possibleActions = new ObjectProperty<>("possibleActions", this);
	private final ObjectProperty<TestAction> action = new ObjectProperty<>("action", this);
	private final ObjectProperty<List<TestParameterFactory>> possibleSelectors = new ObjectProperty<>(
			"possibleSelectors", this);
	private final ObjectProperty<TestParameterFactory> selector = new ObjectProperty<>("selector", this);
	private final ObjectProperty<TestParameterValue> selectorValues = new ObjectProperty<>("selectorValues", this,
			ExportableTestParameterValue.NO_VALUE);
	private final ObjectProperty<List<TestParameterFactory>> possibleActionParameters = new ObjectProperty<>(
			"possibleActionParameters", this);
	private final ObjectProperty<TestParameterFactory> actionParameter = new ObjectProperty<>("actionParameter", this);
	private final ObjectProperty<TestParameterValue> actionParameterValues = new ObjectProperty<>(
			"actionParameterValues", this, ExportableTestParameterValue.NO_VALUE);
	private final ObjectProperty<StepClassifier> stepClassifier = new ObjectProperty<>("stepClassifier", this, null);
	private final ObjectProperty<TestDictionary> testDictionary;

	public StepEditorModel(final ModelConfiguration config, ObjectProperty<TestDictionary> testDictionary) {
		super(config);
		this.testDictionary = testDictionary; 
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

	public ObjectProperty<List<TestParameterFactory>> getPossibleSelectors() {
		return possibleSelectors;
	}

	public ObjectProperty<TestParameterFactory> getSelector() {
		return selector;
	}

	public ObjectProperty<TestParameterValue> getSelectorValue() {
		return selectorValues;
	}

	public ObjectProperty<List<TestParameterFactory>> getPossibleActionParameters() {
		return possibleActionParameters;
	}

	public ObjectProperty<TestParameterFactory> getActionParameter() {
		return actionParameter;
	}

	public ObjectProperty<TestParameterValue> getActionParameterValue() {
		return actionParameterValues;
	}

	public ObjectProperty<StepClassifier> getStepClassifier() {
		return stepClassifier;
	}
	
	public <T extends NamedObject> IConverter<T, ObjectTextView<T>> object2Text() {
		return ObjectTextView.converter(o -> testDictionary.getValue().descriptionOf(o).getDescription());
	}
}
