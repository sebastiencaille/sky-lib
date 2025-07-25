package ch.scaille.tcwriter.gui.editors.steps;

import java.util.List;

import ch.scaille.gui.mvc.GuiModel;
import ch.scaille.gui.mvc.factories.ObjectTextView;
import ch.scaille.javabeans.Converters;
import ch.scaille.javabeans.converters.IConverter;
import ch.scaille.javabeans.properties.ListProperty;
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

	private final ListProperty<TestActor> possibleActors = new ListProperty<>("possibleActors", this);
	private final ObjectProperty<TestActor> actor = new ObjectProperty<>("actor", this);
	private final ListProperty<TestAction> possibleActions = new ListProperty<>("possibleActions", this);
	private final ObjectProperty<TestAction> action = new ObjectProperty<>("action", this);
	private final ListProperty<TestParameterFactory> possibleSelectors = new ListProperty<>(
			"possibleSelectors", this);
	private final ObjectProperty<TestParameterFactory> selector = new ObjectProperty<>("selector", this);
	private final ObjectProperty<TestParameterValue> selectorValues = new ObjectProperty<>("selectorValues", this,
			ExportableTestParameterValue.NO_VALUE);
	private final ListProperty<TestParameterFactory> possibleActionParameters = new ListProperty<>(
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

	public ListProperty<TestActor> getPossibleActors() {
		return possibleActors;
	}

	public ObjectProperty<TestActor> getActor() {
		return actor;
	}

	public ListProperty<TestAction> getPossibleActions() {
		return possibleActions;
	}

	public ObjectProperty<TestAction> getAction() {
		return action;
	}

	public ListProperty<TestParameterFactory> getPossibleSelectors() {
		return possibleSelectors;
	}

	public ObjectProperty<TestParameterFactory> getSelector() {
		return selector;
	}

	public ObjectProperty<TestParameterValue> getSelectorValue() {
		return selectorValues;
	}

	public ListProperty<TestParameterFactory> getPossibleActionParameters() {
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
	
	
	public static <T extends IdObject> IConverter<T, ObjectTextView<T>> object2TextRef() {
		final var obj2Text = ObjectTextView.<T, TestDictionary>biObject2Text((_, _) -> null);
		return Converters.converter(prop -> obj2Text.apply(prop , null), ObjectTextView.comp2prop());
	}

	public record ListPropertyDictionary<T>(ObjectProperty<TestDictionary> dictionary, ListProperty<T> property) {
		
	}
	
	public static <T extends IdObject> IConverter<ListPropertyDictionary<T>, List<ObjectTextView<T>>> objects2Texts() {
		final var objectTextFunction = ObjectTextView.<T, TestDictionary>biObject2Text((o, d) -> d.descriptionOf(o).getDescription());
		return Converters.listen(rec -> rec.property().getValue().stream().map(v -> objectTextFunction.apply(v, rec.dictionary().getValue())).toList());
	}
}
