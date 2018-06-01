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
@TCApi(description = "how to buy an item", stepSummary = "how to buy an item", isNavigation = true)
public class BuyActionNavigator {

	private final Consumer<ExampleService> applier;

	public BuyActionNavigator(final Consumer<ExampleService> applier) {
		this.applier = applier;
	}

	/**
	 * Buy from internet
	 *
	 * @return
	 */
	@TCApi(description = "from internet", stepSummary = "from internet")
	public static BuyActionNavigator fromInternet() {
		return new BuyActionNavigator(svc -> svc.openBrowser());
	}

	@TCApi(description = "in a local shop", stepSummary = "in a local shop")
	public static BuyActionNavigator inLocalShop() {
		return new BuyActionNavigator(svc -> svc.goToShop());
	}

	public void apply(final ExampleService svc) {
		applier.accept(svc);
	}

}
