package ch.skymarshall.tcwriter.examples.api.interfaces.selectors;

import java.util.function.Consumer;

import ch.skymarshall.tcwriter.annotations.TCApi;
import ch.skymarshall.tcwriter.examples.ExampleService;

/**
 * @author scaille
 *
 */
@TCApi(description = "how to handle the item once bought")
public class HandleActionSelector {

	private final Consumer<ExampleService> applier;

	public HandleActionSelector(final Consumer<ExampleService> applier) {
		this.applier = applier;
	}

	/**
	 * Buy from internet
	 *
	 * @return
	 */
	@TCApi(description = "get it from the delivery company")
	public static HandleActionSelector deliveredItem() {
		return new HandleActionSelector(svc -> svc.getPackage());
	}

	@TCApi(description = "already took it at the shop")
	public static HandleActionSelector fromShop() {
		return new HandleActionSelector(svc -> {
			// do nothing, we already have it
		});
	}

	public void apply(final ExampleService svc) {
		applier.accept(svc);
	}

}
