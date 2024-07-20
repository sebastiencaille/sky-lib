package ch.scaille.tcwriter.examples.api.interfaces.selectors;

import java.util.function.Consumer;

import ch.scaille.tcwriter.annotations.TCApi;
import ch.scaille.tcwriter.examples.ExampleService;

/**
 * To selects buying actions
 *
 * @author scaille
 *
 */
@TCApi(description = "where to buy the item", humanReadable = "", isSelector = true)
public class BuyingLocationSelector {

	private final Consumer<ExampleService> applier;

	public BuyingLocationSelector(final Consumer<ExampleService> applier) {
		this.applier = applier;
	}

	/**
	 * Buy from internet
	 *
     */
	@TCApi(description = "On internet", humanReadable = "on internet (%s)")
	public static BuyingLocationSelector onInternet(
			@TCApi(description = "url", humanReadable = "url") final String url) {
		return new BuyingLocationSelector(ExampleService::openBrowser);
	}

	@TCApi(description = "In a local shop", humanReadable = "in a local shop")
	public static BuyingLocationSelector inLocalShop() {
		return new BuyingLocationSelector(ExampleService::goToShop);
	}

	public void apply(final ExampleService svc) {
		applier.accept(svc);
	}

}
