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
@TCApi(description = "buy the item")
public class BuyActionSelector {

	private final Consumer<ExampleService> applier;

	public BuyActionSelector(final Consumer<ExampleService> applier) {
		this.applier = applier;
	}

	/**
	 * Buy from internet
	 *
	 * @return
	 */
	@TCApi(description = "from internet")
	public static BuyActionSelector fromInternet() {
		return new BuyActionSelector(svc -> svc.openBrowser());
	}

	@TCApi(description = "in a local shop")
	public static BuyActionSelector inLocalShop() {
		return new BuyActionSelector(svc -> svc.goToShop());
	}

	public void apply(final ExampleService svc) {
		applier.accept(svc);
	}

}
