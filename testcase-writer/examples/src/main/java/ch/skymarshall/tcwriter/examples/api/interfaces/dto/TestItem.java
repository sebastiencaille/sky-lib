package ch.skymarshall.tcwriter.examples.api.interfaces.dto;

import ch.skymarshall.tcwriter.annotations.TCApi;
import ch.skymarshall.tcwriter.examples.ExampleService.ItemKind;

@TCApi(description = "the item you need", humanReadable = "an item")
public class TestItem {

	public final ItemKind itemKind;
	private String brand;
	private int numberOfItems;
	private boolean iso;

	private TestItem(final ItemKind itemKind) {
		this.itemKind = itemKind;
	}

	public String getBrand() {
		return brand;
	}

	public int getNumberOfItems() {
		return numberOfItems;
	}

	public boolean isIso() {
		return iso;
	}

	@TCApi(description = "Number of items", humanReadable = "count")
	public void setNumberOfItems(final int numberOfItems) {
		this.numberOfItems = numberOfItems;
	}

	@TCApi(description = "A brand", humanReadable = "of brand")
	public void setBrandName(final String brand) {
		this.brand = brand;
	}

	@TCApi(description = "ISO", humanReadable = "ISO")
	public void setISO() {
		this.iso = true;
	}

	@TCApi(description = "A coffee machine", humanReadable = "a coffee machine")
	public static TestItem coffeeMachine() {
		return new TestItem(ItemKind.COFFE_MACHINE);
	}

	@TCApi(description = "A coffee machine of a specific brand", humanReadable = "a coffee machine of brand \"%s\"")
	public static TestItem coffeeMachineOfBrand(
			@TCApi(description = "Name of brand", humanReadable = "") final String brandName) {
		final TestItem testItem = new TestItem(ItemKind.COFFE_MACHINE);
		testItem.setBrandName(brandName);
		return testItem;
	}

	@TCApi(description = "A tea pot", humanReadable = "a tea pot")
	public static TestItem teaPot() {
		return new TestItem(ItemKind.TEA_POT);
	}

}
