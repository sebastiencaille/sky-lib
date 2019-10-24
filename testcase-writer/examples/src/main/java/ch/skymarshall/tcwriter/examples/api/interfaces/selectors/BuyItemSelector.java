package ch.skymarshall.tcwriter.examples.api.interfaces.selectors;

import java.util.function.Consumer;

import ch.skymarshall.tcwriter.annotations.TCApi;
import ch.skymarshall.tcwriter.examples.ExampleService;

/**
 * To selects buying actions
 *
 * @author scaille
 *
 */
@TCApi(description = "how to buy an item", humanReadable = "how to buy an item", isSelector = true)
public class BuyItemSelector {

	private final Consumer<ExampleService> applier;

	public BuyItemSelector(final Consumer<ExampleService> applier) {
		this.applier = applier;
	}

	/**
	 * Buy from internet
	 *
	 * @return
	 */
	@TCApi(description = "On internet", humanReadable = "on internet")
	public static BuyItemSelector fromInternet() {
		return new BuyItemSelector(ExampleService::openBrowser);
	}

	@TCApi(description = "In a local shop", humanReadable = "in a local shop")
	public static BuyItemSelector inLocalShop() {
		return new BuyItemSelector(ExampleService::goToShop);
	}

	public void apply(final ExampleService svc) {
		applier.accept(svc);
	}

}
