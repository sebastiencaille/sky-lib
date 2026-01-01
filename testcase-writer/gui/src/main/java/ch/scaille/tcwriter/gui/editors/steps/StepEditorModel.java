package ch.scaille.tcwriter.gui.editors.steps;

import static ch.scaille.javabeans.properties.PropertiesContext.ofProperty;

import java.util.List;

import ch.scaille.gui.mvc.GuiModel;
import ch.scaille.gui.mvc.factories.ObjectTextView;
import ch.scaille.javabeans.converters.Converters;
import ch.scaille.javabeans.converters.IConverterWithContext;
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
import lombok.Getter;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
@Getter
public class StepEditorModel extends GuiModel {

    private final ListProperty<TestActor> possibleActors = new ListProperty<>("possibleActors", this);
    private final ObjectProperty<@Nullable TestActor> actor = new ObjectProperty<>("actor", this, null);
    private final ListProperty<TestAction> possibleActions = new ListProperty<>("possibleActions", this);
    private final ObjectProperty<@Nullable TestAction> action = new ObjectProperty<>("action", this, null);
    private final ListProperty<TestParameterFactory> possibleSelectors = new ListProperty<>(
            "possibleSelectors", this);
    private final ObjectProperty<@Nullable TestParameterFactory> selector = new ObjectProperty<>("selector", this, null);
    private final ObjectProperty<TestParameterValue> selectorValues = new ObjectProperty<>("selectorValues", this,
            ExportableTestParameterValue.NO_VALUE);
    private final ListProperty<TestParameterFactory> possibleActionParameters = new ListProperty<>(
            "possibleActionParameters", this);
    private final ObjectProperty<@Nullable TestParameterFactory> actionParameter = new ObjectProperty<>("actionParameter", this, null);
    private final ObjectProperty<TestParameterValue> actionParameterValues = new ObjectProperty<>(
            "actionParameterValues", this, ExportableTestParameterValue.NO_VALUE);
    private final ObjectProperty<@Nullable StepClassifier> stepClassifier = new ObjectProperty<>("stepClassifier", this, null);
    private final ObjectProperty<TestDictionary> testDictionary;

    public StepEditorModel(final ModelConfiguration.ModelConfigurationBuilder config, ObjectProperty<TestDictionary> testDictionary) {
        super(config);
        this.testDictionary = testDictionary;
    }

    public static <T extends IdObject> IConverterWithContext<T, ObjectTextView<T>, ObjectProperty<TestDictionary>> object2Text(ObjectProperty<TestDictionary> dictionary) {
        final var obj2Text = ObjectTextView.<T, TestDictionary>biObject2Text((o, d) -> d.descriptionOf(o).description());
        final var text2Obj = ObjectTextView.<T>text2Obj();
        return Converters.converter(ofProperty(dictionary),
                (prop, dic) -> obj2Text.apply(prop, dic.getValue()),
                (comp, _) -> text2Obj.apply(comp));
    }


    public static <T extends IdObject> IConverterWithContext<List<T>, List<ObjectTextView<T>>, ObjectProperty<TestDictionary>> objects2Texts(ObjectProperty<TestDictionary> dictionary) {
        final var obj2Text = ObjectTextView.<T, TestDictionary>biObject2Text((o, d) -> d.descriptionOf(o).description());
        return Converters.listen(ofProperty(dictionary),
                (prop, dic) -> prop.stream().map(v -> obj2Text.apply(v, dic.getValue())).toList());
    }
}
