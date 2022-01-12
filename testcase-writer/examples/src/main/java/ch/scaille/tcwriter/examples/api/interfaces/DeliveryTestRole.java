package ch.scaille.tcwriter.examples.api.interfaces;

import ch.scaille.tcwriter.annotations.TCAction;
import ch.scaille.tcwriter.annotations.TCActors;
import ch.scaille.tcwriter.annotations.TCApi;
import ch.scaille.tcwriter.annotations.TCRole;
import ch.scaille.tcwriter.examples.ExampleService;

@TCRole(description = "A delivery company", humanReadable = "delivery company")
@TCActors("delivery guy|deliveryGuy|DeliveryTestRole")
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
