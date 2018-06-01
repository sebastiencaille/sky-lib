package ch.skymarshall.tcwriter.examples.api.interfaces;

import org.junit.Assert;

import ch.skymarshall.tcwriter.annotations.TCApi;
import ch.skymarshall.tcwriter.annotations.TCRole;
import ch.skymarshall.tcwriter.examples.ExampleService;
import ch.skymarshall.tcwriter.examples.api.interfaces.dto.TestItem;
import ch.skymarshall.tcwriter.examples.api.interfaces.navigators.BuyActionNavigator;
import ch.skymarshall.tcwriter.examples.api.interfaces.navigators.HandleActionNavigator;

@TCRole(description = "A customer")
public class CustomerTestRole extends Assert {

	private final ExampleService testedService;

	public CustomerTestRole(final ExampleService testedService) {
		this.testedService = testedService;
	}

	@TCApi(description = "Buy an item", stepSummary = "buys")
	public void buy(final BuyActionNavigator navigator, final TestItem newItem) {
		// the selector defines all the actions required to apply/check you data (could
		// be button clicks on some
		// hmi, ...)
		navigator.apply(testedService);
		testedService.buy(newItem.itemKind);
	}

	@TCApi(description = "check the delivered package", stepSummary = "checks that the delivered item is")
	public void checkPackage(final HandleActionNavigator navigator, final TestItem handledItem) {
		navigator.apply(testedService);
		assertEquals(testedService.getOwnedItem(), handledItem.itemKind);
	}

	@TCApi(description = "Resell the item", stepSummary = "resels the item for {0}$")
	public void resellOwnedItem(final int price) {
		testedService.reset();
	}

	@TCApi(description = "Look for another brand", stepSummary = "finds another brand")
	public String findAnotherBrand() {
		return "MidClass";
	}

	@TCApi(description = "Keep a note", stepSummary = "keeps a note ({0})")
	public void keepNote(final String note) {
		// naah
	}

}