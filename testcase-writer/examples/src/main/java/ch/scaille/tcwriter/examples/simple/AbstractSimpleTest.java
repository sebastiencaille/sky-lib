package ch.scaille.tcwriter.examples.simple;

import ch.scaille.tcwriter.annotations.TCActor;
import ch.scaille.tcwriter.annotations.TCActors;
import ch.scaille.tcwriter.examples.ExampleService;
import ch.scaille.tcwriter.recorder.RecorderTestActors;
import org.junit.jupiter.api.BeforeEach;

@TCActors({
        @TCActor(variable = "customer", humanReadable = "customer", description = "A customer", role = CustomerTestRole.class),
        @TCActor(variable = "deliveryGuy", humanReadable = "delivery guy", description = "A delivery guy", role = DeliveryTestRole.class)
})
public class AbstractSimpleTest {
    protected CustomerTestRole customer;
    protected DeliveryTestRole deliveryGuy;

    @BeforeEach
    public void initActors() {
        final var testedService = new ExampleService();
        customer = RecorderTestActors.register(new CustomerTestRole(testedService), "customer", null);
        deliveryGuy = RecorderTestActors.register(new DeliveryTestRole(testedService), "deliveryGuy", null);
    }
}
