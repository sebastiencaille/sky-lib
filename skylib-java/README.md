**MVC POC**

Key points
* The Model is made of Properties (basically, a typed value + listeners)
* The dynamic properties of all the visual components are always driven by the properties
  (The visual components are never directly linked together)
* The properties of the model and the visual components are bound through converters
* The MVC model can be generated from the application model

Complete example [[Screenshot](../screenshots/MVC_Full_TC.png)][[Model](lib-gui-examples/src/main/java/ch/skymarshall/example/gui/controller/impl/ControllerExampleModel.java)] [[View](lib-gui-examples/src/main/java/ch/skymarshall/example/gui/controller/impl/ControllerExampleView.java)] 

Example
(to display a boolean property as a checkbox and as a String)
```java
protected final BooleanProperty booleanProperty = ...;

JCheckBox booleanEditor = new JCheckBox();
booleanProperty.bind(selected(booleanEditor));

JTextField stringEditor = new JTextField();
booleanProperty.bind(booleanToString()).bind(value(stringEditor));
```
Working with selections

_When staticListSelection is updated_
1. the dynamicListSelectionProperty is detached from dynamicListEditor, thanks to detachOnUpdateOf
1. the dynamicListEditor is updated
1. the dynamicListSelectionProperty is re-attached
1. the dynamicListSelectionProperty is re-applied to restore the selection

```java
JList<String> dynamicListEditor = new JList<>();
staticListSelection.bind(new DynamicListContentConverter()).bind(values(dynamicListEditor));

final ObjectProperty<String> dynamicListSelectionProperty = model.getDynamicListObjectProperty();
dynamicListSelectionProperty.bind(selection(dynamicListEditor)).addDependency(detachOnUpdateOf(staticListSelection));

```

**List Model**

Key points
* The list is always sorted (for fast search)
* The list can be filtered
* The list can be stacked (1 parent, many children) 
* startEditingValue(editedValue) must be called before editing the value (editedValue only containing the values required for sorting)
* stopEditingValue() must be called to validate the edition and propagate the change

Example [[Code](lib-gui-java8/src/test/java/ch/skymarshall/gui/model/ListModelBasicTest.java)] [[Filters Example](lib-gui-java8/src/test/java/ch/skymarshall/gui/model/FilterObjectModelTest.java)]

```java
IListView<TestObject> VIEW = ListViews.sorted((o1, o2) -> o1.val - o2.val);
ListModel<TestObject> model = new RootListModel<>(VIEW);
ListModel<TestObject> childModel = new ChildListModel<>(model);

TestObject toMove = new TestObject(4);
model.insert(new TestObject(1));
model.insert(new TestObject(3));
model.insert(toMove);
checkModel(childModel, 1, 3, 4);

model.startEditingValue(toMove);
toMove.val = 2;
model.stopEditingValue();
checkModel(childModel, 1, 2, 3);
```
It's possible to control the filter using the MVC concept  [[Code](lib-gui-examples/src/main/java/ch/skymarshall/example/gui/model/impl/TableModelExampleView.java)]

```java
final DynamicView listDynamicView = new DynamicView();
BooleanProperty reverseOrder = ...
model.reverseOrder.bind(selected(... some checkbox ...));
model.reverseOrder.bind(listDynamicView.reverseOrder());

ListModel<TestObject> model = new RootListModel<>(ListViews.sorted(NATURAL_ORDER));
ListModel<TestObject> filteredModel = new ChildListModel<>(model, view);
```

**Table Model**

Key points
* The columns are defined using an Enum
* The model is a ListModel
* The column can have a fixed size or fill the size of the table
 
Model Example [[Model](lib-gui-examples/src/main/java/ch/skymarshall/example/gui/TestObjectTableModel.java)] [[View](lib-gui-examples/src/main/java/ch/skymarshall/example/gui/model/impl/TableModelExampleView.java)]

```java
public class TestObjectTableModel extends ListModelTableModel<TestObject, Columns> {

	public enum Columns { A_FIRST_VALUE, A_SECOND_VALUE	}
	
	public boolean isCellEditable(final int rowIndex, final int columnIndex) {
		return Columns.A_FIRST_VALUE.ordinal() == columnIndex;
	}

   	protected Object getValueAtColumn(final TestObject object, final Columns column) {
		switch (column) {
			case A_FIRST_VALUE: ...
```
Tuning column size: each column is contributing to the table's column size

```java
final ContributionTableColumnModel<StepsTableModel.Column> columnModel = new ContributionTableColumnModel<>(table);
columnModel.install();
columnModel.configureColumn(ContributionTableColumn.fixedColumn(Column.STEP, 20, new DefaultTableCellRenderer()));
columnModel.configureColumn(ContributionTableColumn.fixedColumn(Column.ACTOR, 120, new DefaultTableCellRenderer()));
columnModel.configureColumn(ContributionTableColumn.gapColumn(...)); // Fills the rest of the table 

Arrays.stream(Column.values()).forEach(c -> stepsTable.getColumn(c).setCellEditor(new Editor()));
```

**Generic GUI to edit simple Objects**

The editor is made of
* A widget specific view (a dialog, panel, ...)
* A model 
* An adapter that binds the widget and the adapter 

[[Screenshot](../screenshots/Generic_Editor.png)]
[[Example](lib-gui-examples/src/main/java/ch/skymarshall/example/gui/tools/GenericEditorLauncher.java)]

```java
		EditedObject obj = ... ;
		final SwingGenericEditorDialog view = new SwingGenericEditorDialog(null, "Test",
				Dialog.ModalityType.DOCUMENT_MODAL);
		final GenericEditorAdapter<EditedObject, Component> editor = new GenericEditorAdapter<>(view,
				new GenericEditorClassModel<>(EditedObject.class));
					editor.apply();
		SwingUtilities.invokeLater(() -> {
			editor.load(obj);
			view.setVisible(true);
			System.out.println(obj);
		});
```
