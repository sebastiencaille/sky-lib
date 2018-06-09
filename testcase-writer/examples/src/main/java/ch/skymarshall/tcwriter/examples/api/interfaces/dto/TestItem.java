package ch.skymarshall.tcwriter.examples.api.interfaces.dto;

import ch.skymarshall.tcwriter.annotations.TCApi;
import ch.skymarshall.tcwriter.examples.ExampleService.ItemKind;

@TCApi(description = "the item you need", stepSummary = "an item")
public class TestItem {

	public final ItemKind itemKind;
	private String brand;

	private TestItem(final ItemKind itemKind) {
		this.itemKind = itemKind;
	}

	public String getBrand() {
		return brand;
	}

	@TCApi(description = "a coffee machine", stepSummary = "a coffee machine")
	public static TestItem coffeeMachine() {
		return new TestItem(ItemKind.COFFE_MACHINE);
	}

	@TCApi(description = "a coffee machine of a specific brand", stepSummary = "a coffee machine of brand")
	public static TestItem coffeeMachineOfBrand(final String brandName) {
		final TestItem testItem = new TestItem(ItemKind.COFFE_MACHINE);
		testItem.setBrandName(brandName);
		return testItem;
	}

	@TCApi(description = "a tea pot", stepSummary = "a tea pot")
	public static TestItem teaPot() {
		return new TestItem(ItemKind.TEA_POT);
	}

	@TCApi(description = "a brand", stepSummary = "of brand")
	public void setBrandName(final String brand) {
		this.brand = brand;
	}
}
