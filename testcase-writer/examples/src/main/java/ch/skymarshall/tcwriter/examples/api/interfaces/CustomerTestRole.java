package ch.skymarshall.tcwriter.examples.api.interfaces;

import org.junit.Assert;

import ch.skymarshall.tcwriter.annotations.TCApi;
import ch.skymarshall.tcwriter.annotations.TCRole;
import ch.skymarshall.tcwriter.examples.ExampleService;
import ch.skymarshall.tcwriter.examples.api.interfaces.dto.TestItem;
import ch.skymarshall.tcwriter.examples.api.interfaces.selectors.BuyItemSelector;
import ch.skymarshall.tcwriter.examples.api.interfaces.selectors.HandlePackageSelector;

@TCRole(description = "A customer", stepSummary = "customer")
public class CustomerTestRole extends Assert {

	private final ExampleService testedService;

	public CustomerTestRole(final ExampleService testedService) {
		this.testedService = testedService;
	}

	@TCApi(description = "Buy an item", humanReadable = "go %s and buy %s")
	public void buy(final BuyItemSelector selector, final TestItem newItem) {
		// the selector defines all the actions required to apply/check you data (could
		// be button clicks on some
		// gui, ...)
		selector.apply(testedService);
		testedService.buy(newItem.itemKind);
	}

	@TCApi(description = "Check the packaged item", humanReadable = "get %s and check that the packaged item is %s")
	public void checkPackage(final HandlePackageSelector selector, final TestItem handledItem) {
		selector.apply(testedService);
		assertEquals(testedService.getOwnedItem(), handledItem.itemKind);
	}

	@TCApi(description = "Resell the item", humanReadable = "resell the item for %s$")
	public void resellOwnedItem(final int price) {
		testedService.reset();
	}

	@TCApi(description = "Look for another brand", humanReadable = "find another brand")
	public String findAnotherBrand() {
		return "MidClass";
	}

	@TCApi(description = "Keep a note", humanReadable = "keep the note \"%s\"")
	public void keepNote(@TCApi(description = "a note", humanReadable = "a note") final String note) {
		// noop
	}

}