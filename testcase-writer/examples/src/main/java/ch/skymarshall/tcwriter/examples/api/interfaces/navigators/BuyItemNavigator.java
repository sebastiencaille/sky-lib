package ch.skymarshall.tcwriter.examples.api.interfaces.navigators;

import java.util.function.Consumer;

import ch.skymarshall.tcwriter.annotations.TCApi;
import ch.skymarshall.tcwriter.examples.ExampleService;

/**
 * To selects buying actions
 *
 * @author scaille
 *
 */
@TCApi(description = "how to buy an item", humanReadable = "how to buy an item", isNavigation = true)
public class BuyItemNavigator {

	private final Consumer<ExampleService> applier;

	public BuyItemNavigator(final Consumer<ExampleService> applier) {
		this.applier = applier;
	}

	/**
	 * Buy from internet
	 *
	 * @return
	 */
	@TCApi(description = "from internet", humanReadable = "from internet")
	public static BuyItemNavigator fromInternet() {
		return new BuyItemNavigator(ExampleService::openBrowser);
	}

	@TCApi(description = "in a local shop", humanReadable = "in a local shop")
	public static BuyItemNavigator inLocalShop() {
		return new BuyItemNavigator(ExampleService::goToShop);
	}

	public void apply(final ExampleService svc) {
		applier.accept(svc);
	}

}
