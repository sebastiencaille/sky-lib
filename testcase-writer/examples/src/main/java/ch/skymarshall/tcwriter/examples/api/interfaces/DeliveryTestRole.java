package ch.skymarshall.tcwriter.examples.api.interfaces;

import ch.skymarshall.tcwriter.annotations.TCApi;
import ch.skymarshall.tcwriter.annotations.TCRole;
import ch.skymarshall.tcwriter.examples.ExampleService;

@TCRole(description = "A delivery company", humanReadable = "delivery company")
public class DeliveryTestRole {

	private final ExampleService testedService;

	public DeliveryTestRole(final ExampleService testedService) {
		this.testedService = testedService;
	}

	@TCApi(description = "Deliver item", humanReadable = "deliver the item")
	public void deliverItem() {
		testedService.delivered();
	}

}
