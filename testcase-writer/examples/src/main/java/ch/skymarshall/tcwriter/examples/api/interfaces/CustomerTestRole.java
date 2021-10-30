package ch.skymarshall.tcwriter.examples.api.interfaces;

import org.junit.jupiter.api.Assertions;

import ch.skymarshall.tcwriter.annotations.TCApi;
import ch.skymarshall.tcwriter.annotations.TCRole;
import ch.skymarshall.tcwriter.examples.ExampleService;
import ch.skymarshall.tcwriter.examples.api.interfaces.dto.TestItem;
import ch.skymarshall.tcwriter.examples.api.interfaces.selectors.BuyingLocationSelector;
import ch.skymarshall.tcwriter.examples.api.interfaces.selectors.PackageDeliverySelector;

@TCRole(description = "A customer", humanReadable = "customer")
@SuppressWarnings("java:S5960")
public class CustomerTestRole extends Assertions {

	private final ExampleService testedService;

	public CustomerTestRole(final ExampleService testedService) {
		this.testedService = testedService;
	}

	@TCApi(description = "Buy an item", humanReadable = "I go %s and buy %s")
	public void buy(final BuyingLocationSelector selector, final TestItem newItem) {
		// the selector defines all the actions required to apply/check you data (could
		// be button clicks on some
		// gui, ...)
		selector.apply(testedService);
		testedService.buy(newItem.itemKind);
	}

	@TCApi(description = "Check the packaged item", humanReadable = "I get %s and check that the packaged item is %s")
	public void checkPackage(final PackageDeliverySelector selector, final TestItem handledItem) {
		selector.apply(testedService);
		assertEquals(testedService.getOwnedItem(), handledItem.itemKind);
	}

	@TCApi(description = "Resell the item", humanReadable = "I resell the item for %s$")
	public void resellOwnedItem(final int price) {
		testedService.reset();
	}

	@TCApi(description = "Look for another brand", humanReadable = "I find another brand")
	public String findAnotherBrand() {
		return "MidClass";
	}

	@TCApi(description = "Keep a note", humanReadable = "I keep the note \"%s\"")
	public void keepNote(@TCApi(description = "a note", humanReadable = "a note") final String note) {
		// noop
	}

}