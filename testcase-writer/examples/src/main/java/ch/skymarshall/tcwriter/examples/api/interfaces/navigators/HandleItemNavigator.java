package ch.skymarshall.tcwriter.examples.api.interfaces.navigators;

import java.util.function.Consumer;

import ch.skymarshall.tcwriter.annotations.TCApi;
import ch.skymarshall.tcwriter.examples.ExampleService;

/**
 * @author scaille
 *
 */
@TCApi(description = "how to handle the item once bought", humanReadable = "how to handle the item once bought", isNavigation = true)
public class HandleItemNavigator {

	private final Consumer<ExampleService> applier;

	public HandleItemNavigator(final Consumer<ExampleService> applier) {
		this.applier = applier;
	}

	/**
	 * Buy from internet
	 *
	 * @return
	 */
	@TCApi(description = "item delivered by company", humanReadable = "the delivered item")
	public static HandleItemNavigator deliveredItem() {
		return new HandleItemNavigator(ExampleService::getPackage);
	}

	@TCApi(description = "item bought at the shop", humanReadable = "the item bought at the shop")
	public static HandleItemNavigator fromShop() {
		return new HandleItemNavigator(svc -> {
			// do nothing, we already have it
		});
	}

	public void apply(final ExampleService svc) {
		applier.accept(svc);
	}

}
