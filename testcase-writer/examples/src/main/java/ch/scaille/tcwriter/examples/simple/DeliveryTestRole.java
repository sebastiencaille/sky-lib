package ch.scaille.tcwriter.examples.simple;

import ch.scaille.tcwriter.annotations.*;
import ch.scaille.tcwriter.examples.ExampleService;

@TCRole(description = "A delivery company", humanReadable = "delivery company")
public class DeliveryTestRole {

	private final ExampleService testedService;

	public DeliveryTestRole(final ExampleService testedService) {
		this.testedService = testedService;
	}

	@TCApi(description = "Deliver item", humanReadable = "I deliver the item")
	@TCAction
	public void deliverItem() {
		testedService.delivered();
	}

}
