package ch.skymarshall.tcwriter.examples.api.interfaces.dto;

import ch.skymarshall.tcwriter.annotations.TCApi;
import ch.skymarshall.tcwriter.examples.ExampleService.ItemKind;

@TCApi(description = "the item you need")
public class TestItem {

	public ItemKind itemKind;
	private String brand;

	private TestItem(final ItemKind itemKind) {
		this.itemKind = itemKind;
	}

	@TCApi(description = "a coffee machine")
	public static TestItem coffeeMachine() {
		return new TestItem(ItemKind.COFFE_MACHINE);
	}

	@TCApi(description = "a tea pot")
	public static TestItem teaPot() {
		return new TestItem(ItemKind.TEA_POT);
	}

	@TCApi(description = "a brand")
	public void setBrandName(final String brand) {
		this.brand = brand;
	}
}
