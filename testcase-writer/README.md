**Business oriented testing POC**

The key to write business oriented test cases is to have adequate test APIs.

The idea is to have 
* Actors, each actor having a Role
* Each Role contains Actions
* Those Actions are made of
  * The action itself
  * a Selection operation (a Selector, to select the element targeted by the Action)
  * the action's Parameters 
* Factories to create business oriented Selectors and Parameters

Example

![Test Case](../screenshots/TC_Writer.png)


_Api_ [[Code](examples/src/main/java/ch/skymarshall/tcwriter/examples/api/interfaces)]

```java
@TCRole(description = "Customer")
public class CustomerTestRole extends Assert {
	
	@TCApi(description = "Buy an item", humanReadable = "go %s and buy %s")
	public void buy(final BuyingLocationSelector selector, final TestItem newItem) {
		...
	}

	@TCApi(description = "Check the packaged item", humanReadable = "get %s and check that the packaged item is %s")
	public void checkPackage(final PackageDeliverySelector selector, final TestItem handledItem) {
		...
	} 
	...
}

@TCApi(description = "delivery mean", humanReadable = "", isSelector = true)
public class PackageDeliverySelector {

	@TCApi(description = "Item delivered by delivery company", humanReadable = "the delivered package")
	public static PackageDeliverySelector deliveredItem() {
		... 
	}
	...
}


@TCApi(description = "an item you can buy", humanReadable = "")
public class TestItem {

	@TCApi(description = "A coffee machine", humanReadable = "a coffee machine")
	public static TestItem coffeeMachine() {
		...		
	}

	@TCApi(description = "Number of items", humanReadable = "count")
	public void setNumberOfItems(final int numberOfItems) {
		...
	}
	...
}
```

_Java test case_ [[Code](examples/src/main/java/ch/skymarshall/tcwriter/examples/SimpleTest.java)]

```java
CustomerTestRole customer = new CustomerTestRole(testedService); // Aka A customer
...
customer.buy(onInternet(), coffeeMachine());
deliveryGuy.deliverItem(); // Another actor of the system
customer.checkPackage(deliveredItem(), coffeeMachine());
```
You can actually read

Actor | Action | Selector | Parameter
----- | ------ | --------- | ---------
A customer     | Buy an item                              | From internet| A coffee machine 
A delivery guy | Deliver an item                  | ||
A customer     | Check that the delivered item is || A coffee machine |

This way of structuring the api should be suitable for
* writing test cases using some GUI based application. That is, the application may allow the user to select (based on the data types)
  1. The Actor ("A customer")
  1. Based on the selected Actor's role, the Action "Buy an item"
  1. Based on the selected Action, the Selector "On internet"
  1. Based on the selected Action, the parameter "A coffee machine"
* generating "readable" test reports

```
As customer, I go on internet and buy a coffee machine of brand "OldSchool" (ISO: yes)
As delivery company, I deliver the item
As customer, I get the delivered package and check that the packaged item is a coffee machine of brand "OldSchool" (ISO: yes)
As customer, I resell the item for 10$
As customer, I go in a local shop and buy a tea pot
As customer, I get the package bought at the shop and check that the packaged item is a tea pot
As customer, I resell the item for 10$
As customer, I find another brand
As customer, I keep the note "[Value of step 8: MidClass]"
```

* generating java test cases [[Code](examples/src/test/java/ch/skymarshall/tcwriter/examples/GeneratedTest.java)]

* storing the test in a "data description" format (JSON, XML, ...) [[Code](examples/src/main/resources/models)]  [[Code](examples/src/main/resources/testCase.json)]

* A demonstration GUI is available here [[Code](examples/src/main/java/ch/skymarshall/tcwriter/examples/gui/ExampleTCEditor.java)]
  * The test model is automatically built from the java classes.
  * The testcase is recorded from the execution of [[SimpleTest](examples/src/main/java/ch/skymarshall/tcwriter/examples/SimpleTest.java)] (thanks to aspectj)


  
