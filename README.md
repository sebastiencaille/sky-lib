## Concepts

**Business oriented test concept**

The key to write business oriented test cases is to have an adapted test api
The concept is to have 
* Actors, each actor having a Role
* Role contains Actions
* Actions are made of
  * an action
  * a navigation operation
  * the action parameters (usually one, maybe two)
* Factories to creation navigation operations and action parameters

Example

_Api_
````
@TCRole(description = "Customer")
public class CustomerTestRole extends Assert {
   ...

@TCApi(description = "Buy an item", stepSummary = "buy")

	public void buy(final BuyActionNavigator navigator, final TestItem newItem) {
		navigator.apply(testedService);
		testedService.buy(newItem.itemKind); // Then use application api
	}

	@TCApi(description = "check the delivered package", stepSummary = "checks that the delivered item is")
	public void checkPackage(final HandleActionNavigator navigator, final TestItem handledItem) {
		navigator.apply(testedService);
		assertEquals(testedService.getOwnedItem(), handledItem.itemKind); // Then use application api	
	} 
}

@TCApi(description = "how to buy an item", stepSummary = "how to buy an item", isNavigation = true)
public class BuyActionNavigator {

	private final Consumer<ExampleService> applier;

	public BuyActionNavigator(final Consumer<ExampleService> applier) {
		this.applier = applier;
	}

	@TCApi(description = "from internet", stepSummary = "from internet")
	public static BuyActionNavigator fromInternet() {
		return new BuyActionNavigator(svc -> svc.openBrowser()); // Use application api
	}
}


@TCApi(description = "the item you need", stepSummary = "an item")
public class TestItem {
...
	@TCApi(description = "a coffee machine", stepSummary = "a coffee machine")
	public static TestItem coffeeMachine() {
		...		
	}
...
}
````

_Test_
````
CustomerTestRole customer = new CustomerTestRole(testedService); // Aka A customer
TestItem coffeeMachine = TestItem.coffeeMachine();
customer.buy(fromInternet(), coffeeMachine);
deliveryGuy.deliverItem(); // Another actor of the system
customer.checkPackage(deliveredItem(), coffeeMachine);
````
You can actually read

Actor | Action | Parameter | Navigator
----- | ------ | --------- | ---------
A customer     | buy an item                              | a coffee machine | from internet
A delivery guy | deliver an item                  | ||
A customer     | check that the delivered item is | a coffee machine |

This way of structuring the api should be suitable to
* write test cases using some high level application. That is, the application may allow the user to select (based on the data type)
  * First the actor ("A customer")
  * Then, from the actor's role, the action "Buy an item"
  * Then, from the action, the navigator "from internet"
  * Then, from the action, the parameter "a coffee machine"
* generate "readable" test reports
````
Step 1: As customer, I buy in a local shop: a coffee machine of brand: DeLuxeBrand
Step 2: As customer, I check that the packaged item is the item bought at the shop: a coffee machine of brand DeLuxeBrand
Step 3: As customer, I resell the item (in $): 10
Step 4: As customer, I find another brand
Step 5: As customer, I keep a note: another brand (from step 4)
````

**Data flow management concept**

The idea is to provide tools to help keeping control of the software's structure

The concept is the following: 
* The description of the structure is based on the concept of flows
* A flow describes the structure of the information's exchange in the application 
* A service is basically structured the following way:
  * A data multiplexer: the service may receive different types of input data, which are multiplexed in a type supported by the service
  * At some point, the multiplexed data is sent to the service's processing unit (the Action)
  * the output of the Action is de-multiplexed into many output data
* The services are exchanging information through the input/output data
* The description is written in a description language (json, xml, ...)
* Generators are used to 
  * Create the operational code
  * Create unit tests (to test the structure, using mock objects)
  * Create a visual description of the structure (eg dependency graphs using dot)
* The description of the structure may show the input/output data and actions used during a specific execution (test case or application) 

**MVC concept**

Key points
* The Model is made of Properties (basically, a typed value and listeners)
* The dynamic properties of all the graphical components are always driven by properties
* The dynamic properties of the graphical components are never directly linked together 
* Properties of the model and properties of the graphical components are bound through converters
* The MVC model can be generated from the application model

Example
````
protected final BooleanProperty booleanProperty = ...;

JCheckBox booleanEditor = new JCheckBox();
booleanProperty.bind(selected(booleanEditor));

JTextField stringEditor = new JTextField();
booleanProperty.bind(booleanToString()).bind(value(stringEditor));
````
Working with selections

_When propertyThatDrivesTheListValues is updated_
1. dynamicListSelectionProperty is detached from dynamicListSelectionEditor
1. dynamicListSelectionEditor is updated
1. dynamicListSelectionProperty is re-applied to restore the selection
1. dynamicListSelectionProperty is re-attached
````
JList<String> dynamicListSelectionEditor = new JList<>();
ObjectProperty<String> propertyThatDrivesTheListValues = ... 
propertyThatDrivesTheListValues.bind(...some converter...).bind(values(dynamicListSelectionEditor));

ObjectProperty<String> dynamicListSelectionProperty = ...;
dynamicListSelectionProperty.bind(selection(dynamicListSelectionEditor)).addDependency(detachOnUpdateOf(propertyThatDrivesTheListValues)); 
````

**List Model**

Key points
* The list is always sorted (for fast search)
* The list can be filtered
* The list can be stacked (1 parent, many children) 
* startEditingValue(editedValue) must be called before editing the value (editedValue only containing the values required for sorting)
* stopEditingValue() must be called to validate the edition and propagate the change

Example
````
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
````
Controlling filter using the MVC concept (TableModelExampleView.java)
````
final DynamicView view = new DynamicView();
BooleanProperty reverseOrder = ...
reverseOrder.bind(selected(... some checkbox ...));
reverseOrder.bind(view.reverseOrder());
ListModel<TestObject> model = new RootListModel<>(ListViews.sorted(NORMAL_ORDER));
ListModel<TestObject> filteredModel = new ChildListModel<>(model, view);
````

**Table model handling**

Key points
* The columns are defined by an Enum
* The model is a ListModel
* The column can have a fixed size or fill the size of the table
 
Example
````
public class TestObjectTableModel extends ListModelTableModel<TestObject, Columns> {

	public enum Columns { A_FIRST_VALUE, A_SECOND_VALUE	}
	
	public boolean isCellEditable(final int rowIndex, final int columnIndex) {
		return Columns.A_FIRST_VALUE.ordinal() == columnIndex;
	}

   	protected Object getValueAtColumn(final TestObject object, final Columns column) {
		switch (column) {
			case A_FIRST_VALUE: ...
````
Tuning columns
````
final ContributionTableColumnModel<StepsTableModel.Column> columnModel = new ContributionTableColumnModel<>(table);
columnModel.install();
columnModel.configureColumn(ContributionTableColumn.fixedColumn(Column.STEP, 20, new DefaultTableCellRenderer()));
columnModel.configureColumn(ContributionTableColumn.fixedColumn(Column.ACTOR, 120, new DefaultTableCellRenderer()));
columnModel.configureColumn(ContributionTableColumn.gapColumn(...)); // Will fill the rest of the table 

Arrays.stream(Column.values()).forEach(c -> stepsTable.getColumn(c).setCellEditor(new Editor()));
````


