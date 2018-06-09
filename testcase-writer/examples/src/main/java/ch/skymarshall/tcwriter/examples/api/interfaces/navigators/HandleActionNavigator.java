package ch.skymarshall.tcwriter.examples.api.interfaces.navigators;

import java.util.function.Consumer;

import ch.skymarshall.tcwriter.annotations.TCApi;
import ch.skymarshall.tcwriter.examples.ExampleService;

/**
 * @author scaille
 *
 */
@TCApi(description = "how to handle the item once bought", stepSummary = "how to handle the item once bought", isNavigation = true)
public class HandleActionNavigator {

	private final Consumer<ExampleService> applier;

	public HandleActionNavigator(final Consumer<ExampleService> applier) {
		this.applier = applier;
	}

	/**
	 * Buy from internet
	 *
	 * @return
	 */
	@TCApi(description = "item delivered by company", stepSummary = "the delivered item")
	public static HandleActionNavigator deliveredItem() {
		return new HandleActionNavigator(svc -> svc.getPackage());
	}

	@TCApi(description = "item bought at the shop", stepSummary = "the item bought at the shop")
	public static HandleActionNavigator fromShop() {
		return new HandleActionNavigator(svc -> {
			// do nothing, we already have it
		});
	}

	public void apply(final ExampleService svc) {
		applier.accept(svc);
	}

}
