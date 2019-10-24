package ch.skymarshall.tcwriter.examples.api.interfaces.selectors;

import java.util.function.Consumer;

import ch.skymarshall.tcwriter.annotations.TCApi;
import ch.skymarshall.tcwriter.examples.ExampleService;

/**
 * @author scaille
 *
 */
@TCApi(description = "how to handle the item once bought", humanReadable = "how to handle the item once bought", isSelector = true)
public class HandlePackageSelector {

	private final Consumer<ExampleService> applier;

	public HandlePackageSelector(final Consumer<ExampleService> applier) {
		this.applier = applier;
	}

	/**
	 * Buy from internet
	 *
	 * @return
	 */
	@TCApi(description = "Item delivered by company", humanReadable = "the delivered package")
	public static HandlePackageSelector deliveredItem() {
		return new HandlePackageSelector(ExampleService::getPackage);
	}

	@TCApi(description = "Item bought at the shop", humanReadable = "the package bought at the shop")
	public static HandlePackageSelector fromShop() {
		return new HandlePackageSelector(svc -> {
			// do nothing, we already have it
		});
	}

	public void apply(final ExampleService svc) {
		applier.accept(svc);
	}

}
