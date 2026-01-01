# MVC POC

The model/ui related code is located in the project [[lib-gui](lib-gui) | [lib-gui-examples](lib-gui-examples)].

The testing related code is located in the project [[lib-testing](lib-testing)].

# Testing

When developing end 2 end tests, the main challenge is to wait until actions can be performed / values can be validated.

This can be done by try to perform actions / assertions many times. The following construction can be used:
```java
element.failUnless().clicked();
element.fail("Click failed").timingOut(Duration.ofSeconds(10)).unless().clicked();
element.failUnless().satisfied(JComponent::isEnabled);

boolean clicked = element.evaluateThat().clicked();
boolean clicked = element.evaluate().timingOut(Duration.ofSeconds(10)).that().clicked();
boolean clicked = element.report("Click failed").unless().clicked();
```


# Model Properties

**Key points**
* The Model is made of Properties (basically, a typed value + listeners).
* The model properties are driving the components properties.
  This is similar to JavaFX, but model-centric instead of component centric.
* The properties of the model and the visual components are bound through converters (possibly weak)
* Properties can be derived from other properties (through records or a cascade of lambda expressions).
* The MVC model can be generated from the application model

Complete example: [[Screenshot](../screenshots/MVC_Full_TC.png)][[Model](lib-gui-examples/src/main/java/ch/scaille/example/gui/controller/impl/ControllerExampleModel.java)] [[View](lib-gui-examples/src/main/java/ch/scaille/example/gui/controller/impl/ControllerExampleView.java)] 

**Basic Examples**  
(to display a boolean property as a checkbox and as a String)
```java
private final BooleanProperty booleanProperty = ...;

JCheckBox booleanEditor = new JCheckBox();
booleanProperty.bind(selected(booleanEditor));

JTextField stringEditor = new JTextField();
booleanProperty.bind(booleanToString()).bind(value(stringEditor));
```

Deriving a value from multiple properties (lambda version)
```
    private final PropertiesAggregator<Integer> lambdaAggregator = new PropertiesAggregator<Integer>("lambdaAggregator", group)
            .addWithMore(intProperty1, intProperty2, intProperty3, intProperty4,
                    (p1, p2, p3, p4, more) ->
                            more.add(intProperty5, intProperty6, (p5, p6) ->
                                    p1.get() + p2.get() + p3.get() + p4.get() + p5.get() + p6.get()));

```
Deriving a value from multiple properties (record version)
```
    private record P12(IntProperty p1, IntProperty p2) {};

    private final PropertiesAggregator<Integer> recordAggregator = new ch.scaille.javabeans.properties.PropertiesAggregator<Integer>("recordAggregator", group)
            .of(PropertiesContext.ofRecord(new P12(intProperty1, intProperty2)),
                    k -> k.p1().getValue() + k.p2().getValue());
```
Binding with a record as context.
```
    public record Context(IntProperty p1, IntProperty p2) {};
...
    private final PropertiesContext<Context> p1p2Context = PropertiesContext.ofRecord(new Context(intProperty1, intProperty2));

    intProperty3.bind(p1p2Context,
                (value3, k) -> k.p1.getValue() + k.p2.getValue() + value3,
                (r, k) -> r)
                .listen(computations::add);

```

**Working with selections**  

The lists/tables/... selection is bound to a property. This property is updated when the selection has changed, and the selection is updated when the property is updated.  
When the content of the list/table/... is updated, the selection lost.  
The solution is to detach the selection binding before updating the component, and re-attach it after the component has changed   

In the example, when staticListSelection is updated:
1. the dynamicListSelectionProperty is detached from dynamicListEditor, thanks to detachOnUpdateOf
2. the dynamicListEditor is updated and the selection is lost 
3. the dynamicListSelectionProperty is re-attached
4. the dynamicListSelectionProperty is re-applied to restore the selection

```java
JList<String> dynamicListEditor = new JList<>();
staticListSelection.bind(new DynamicListContentConverter()).bind(values(dynamicListEditor));

final ObjectProperty<String> dynamicListSelectionProperty = model.getDynamicListObjectProperty();
dynamicListSelectionProperty.bind(selection(dynamicListEditor)).addDependency(detachOnUpdateOf(staticListSelection));
```

# List Model

**Key points**
* the list is always sorted (for fast search)
* the list can be filtered
* the list can be stacked (1 parent, many children) 
* startEditingValue(editedValue) must be called before editing the value (editedValue only containing the values required for sorting)
* stopEditingValue() must be called to validate the edition, move the edit value at the right place, and propagate the change

Example: [[Code](lib-gui/src/test/java/ch/scaille/gui/model/ListModelBasicTest.java)] [[Filters Example](lib-gui/src/test/java/ch/scaille/gui/model/FilterObjectModelTest.java)]

```java
IListView<TestObject> VIEW = ListViews.sorted((o1, o2) -> o1.val - o2.val);
ListModel<TestObject> model = new RootListModel<>(VIEW);
ListModel<TestObject> childModel = new ChildListModel<>(model);

TestObject toMove = new TestObject(4);
model.insert(new TestObject(1));
model.insert(new TestObject(3));
model.insert(toMove);
checkModel(childModel, 1, 3, 4);

model.editValue(toMove, v -> v.val = 2);
// or
try {
  model.startEditingValue(toMove);
  toMove.val = 2;
} finally {
  model.stopEditingValue();
}
// or
try (IEdition e = model.startEditingValue(toMove)) {
    toMove.val = 2;
}

checkModel(childModel, 1, 2, 3);
```
It is possible to control the filter using the MVC concept  [[Code](lib-gui-examples/src/main/java/ch/scaille/example/gui/model/impl/TableModelExampleView.java)]

```java
final DynamicView listDynamicView = new DynamicView();
BooleanProperty reverseOrder = ...
model.reverseOrder.bind(selected(... some checkbox ...)); // the model is modified by the component
model.reverseOrder.bind(listDynamicView);  // the model directly modifies the view

ListModel<TestObject> model = new RootListModel<>(ListViews.sorted(NATURAL_ORDER));
ListModel<TestObject> filteredModel = new ChildListModel<>(model, listDynamicView);
```

# Table Model

**Key points**
* The columns are defined using an Enum
* The model is a ListModel
* The column can have a fixed size, or adapt it's size according to the table's size
 
Model Example: [[Model](lib-gui-examples/src/main/java/ch/scaille/example/gui/TestObjectTableModel.java)] [[View](lib-gui-examples/src/main/java/ch/scaille/example/gui/model/impl/TableModelExampleView.java)]

```java
import java.util.Objects;

public class TestObjectTableModel extends ListModelTableModel<TestObject, Columns> {

    public enum Columns {A_FIRST_VALUE, A_SECOND_VALUE}

    public boolean isCellEditable(final int rowIndex, final int columnIndex) {
        return Columns.A_FIRST_VALUE.ordinal() == columnIndex;
    }

    protected Object getValueAtColumn(final TestObject object, final Columns column) {
        if (Objects.requireNonNull(column) == Columns.A_FIRST_VALUE) {...
        }
```
**Tuning the table columns**  
The width of each column is computed according to the policy of each column (fixed or % or remaining width)  

```java
final PolicyTableColumnModel<StepsTableModel.Column> columnModel = new PolicyTableColumnModel<>(stepsJTable);
columnModel.install();
Arrays.stream(Column.values()).forEach(c -> stepsJTable.getColumn(c).setCellRenderer(new StepsCellRenderer()));
columnModel.configureColumn(TableColumnPolicy.fixedWidth(Column.BREAKPOINT, 20).apply(new StepStatusRenderer(), new StepStatusEditor()));
columnModel.configureColumn(TableColumnPolicy.fixedWidth(Column.STEP, 20));
columnModel.configureColumn(TableColumnPolicy.fixedWidth(Column.ACTOR, 120).apply(new StepsCellRenderer()));
columnModel.configureColumn(TableColumnPolicy.percentOfAvailableSpace(Column.SELECTOR, 50).apply(new StepsCellRenderer()));
columnModel.configureColumn(TableColumnPolicy.percentOfAvailableSpace(Column.PARAM0, 50).apply(new StepsCellRenderer()));
columnModel.configureColumn(TableColumnPolicy.fixedWidth(Column.TO_VAR, 250).apply(new StepsCellRenderer()));

```

# Generic GUI to edit simple Objects

The editor is made of
* A widget specific view (a dialog, panel, ...)
* A model 
* An adapter that binds the widget and the adapter 

[[Screenshot](../screenshots/Generic_Editor.png)]
[[Example](lib-gui-examples/src/main/java/ch/scaille/example/gui/tools/GenericEditorLauncher.java)]

```java
final SwingGenericEditorDialog view = new SwingGenericEditorDialog(null, "Test",
		Dialog.ModalityType.DOCUMENT_MODAL);
final GenericEditorController<EditedObject> editor = new GenericEditorController<>(view,
			GenericEditorClassModel.builder(EditedObject.class) //
						.addAdapters(new GenericEditorValidationAdapter()) // optionally add validation
						.build());

EditedObject obj = ... ;
SwingUtilities.invokeLater(() -> {
	editor.apply();
	editor.load(obj);
	view.setVisible(true);
	System.out.println(obj);
});
```

