package ch.scaille.tcwriter.examples.simple.selectors;

import java.util.function.Consumer;

import ch.scaille.tcwriter.annotations.TCApi;
import ch.scaille.tcwriter.examples.ExampleService;

/**
 * @author scaille
 *
 */
@TCApi(description = "delivery mean", humanReadable = "", isSelector = true)
public class PackageDeliverySelector {

	private final Consumer<ExampleService> applier;

	public PackageDeliverySelector(final Consumer<ExampleService> applier) {
		this.applier = applier;
	}

	/**
	 * Buy from internet
	 *
     */
	@TCApi(description = "Item delivered by delivery company", humanReadable = "the delivered package")
	public static PackageDeliverySelector deliveredItem() {
		return new PackageDeliverySelector(ExampleService::getPackage);
	}

	@TCApi(description = "Item bought at the shop", humanReadable = "the package bought at the shop")
	public static PackageDeliverySelector fromShop() {
		return new PackageDeliverySelector(_ -> {
			// do nothing, we already have it
		});
	}

	public void apply(final ExampleService svc) {
		applier.accept(svc);
	}

}
