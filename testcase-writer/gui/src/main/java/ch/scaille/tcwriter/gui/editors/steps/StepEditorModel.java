package ch.scaille.tcwriter.gui.editors.steps;

import java.util.List;

import ch.scaille.gui.mvc.GuiModel;
import ch.scaille.gui.mvc.factories.ObjectTextView;
import ch.scaille.javabeans.Converters;
import ch.scaille.javabeans.converters.IConverterWithContext;
import ch.scaille.javabeans.properties.ObjectProperty;
import ch.scaille.tcwriter.model.IdObject;
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

	public ObjectProperty<TestDictionary> getTestDictionary() {
		return testDictionary;
	}
	
	public static <T extends IdObject> IConverterWithContext<T, ObjectTextView<T>, ObjectProperty<TestDictionary>> object2Text() {
		final var objectTextFunction = ObjectTextView.<T, ObjectProperty<TestDictionary>>biObject2Text(
				(o, k) -> k.getValue().descriptionOf(o).getDescription());
		return Converters.converter(objectTextFunction::apply, (c, _) -> ObjectTextView.<T>comp2prop().apply(c));
	}

	public static <T extends IdObject> IConverterWithContext<List<T>, List<ObjectTextView<T>>, ObjectProperty<TestDictionary>> objects2Texts() {
		final var objectTextFunction = ObjectTextView.<T, ObjectProperty<TestDictionary>>biObject2Text(
				(o, k) -> k.getValue().descriptionOf(o).getDescription());
		return Converters.listen((p, k) -> p.stream().map(v -> objectTextFunction.apply(v, k)).toList());
	}
}
