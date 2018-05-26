package ch.skymarshall.tcwriter.examples.api.interfaces;

import ch.skymarshall.tcwriter.annotations.TCActor;
import ch.skymarshall.tcwriter.annotations.TCApi;
import ch.skymarshall.tcwriter.examples.ExampleService;

@TCActor(description = "Delivery company")
public class DeliveryTestActor {

	private final ExampleService testedService;

	public DeliveryTestActor(final ExampleService testedService) {
		this.testedService = testedService;
	}

	@TCApi(description = "deliver item")
	public void deliverItem() {
		testedService.delivered();
	}

}
