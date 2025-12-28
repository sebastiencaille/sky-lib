package ch.scaille.javabeans.properties;

import ch.scaille.javabeans.IPropertiesGroup;
import ch.scaille.javabeans.PropertyChangeSupportController;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

class ConvertersWithContextTest {
    private final IPropertiesGroup group =  PropertyChangeSupportController.mainGroup(this);

    public record Context(IntProperty p1, IntProperty p2) {
        // noop
    }

    private final IntProperty intProperty1 = new IntProperty("p1", group);
    private final IntProperty intProperty2 = new IntProperty("p2", group);
    private final IntProperty intProperty3 = new IntProperty("p3", group);

    private final PropertiesContext<Context> p1p2Context = PropertiesContext.ofRecord(new Context(intProperty1, intProperty2));

    @Test
    void testConverterRecordContext() {
        final var computations = new ArrayList<>();
        intProperty3.bind(p1p2Context,
                (value3, k) -> k.p1.getValue() + k.p2.getValue() + value3,
                (r, k) -> r)
                .listen(computations::add);

        group.transmitChangesBothWays();

        intProperty1.setValue(this, 10);
        intProperty2.setValue(this, 100);
        intProperty3.setValue(this, 1000);

        // 0, 0, 0 are caused by transmitChangesBothWays
        Assertions.assertEquals(List.of(0, 0, 0, 10, 110, 1110), computations);
    }

}
