package ch.skymarshall.tcwriter.examples.api.interfaces;

import org.junit.Assert;

import ch.skymarshall.tcwriter.annotations.TCActor;
import ch.skymarshall.tcwriter.annotations.TCApi;
import ch.skymarshall.tcwriter.examples.ExampleService;
import ch.skymarshall.tcwriter.examples.api.interfaces.dto.TestItem;
import ch.skymarshall.tcwriter.examples.api.interfaces.selectors.BuyActionSelector;
import ch.skymarshall.tcwriter.examples.api.interfaces.selectors.HandleActionSelector;

@TCActor(description = "Customer")
public class CustomerTestRole extends Assert {

	private final ExampleService testedService;

	public CustomerTestRole(final ExampleService testedService) {
		this.testedService = testedService;
	}

	@TCApi(description = "Buy an item")
	public void buy(final BuyActionSelector selector, final TestItem newItem) {
		// the selector defines all the actions required to apply/check you data (could
		// be button clicks on some
		// hmi, ...)
		selector.apply(testedService);
		testedService.buy(newItem.itemKind);
	}

	@TCApi(description = "handle the item package and check it's content")
	public void handleAndCheckPackage(final HandleActionSelector selector, final TestItem handledItem) {
		selector.apply(testedService);
		assertEquals(testedService.getOwnedItem(), handledItem.itemKind);
	}

	@TCApi(description = "Resell the item")
	public void resellOwnedItem() {
		testedService.reset();
	}

}