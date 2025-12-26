package ch.scaille.javabeans.properties;

import ch.scaille.javabeans.IPropertiesGroup;
import ch.scaille.javabeans.PropertyChangeSupportController;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

class PropertiesAggregatorTest {

    private final IPropertiesGroup group =  PropertyChangeSupportController.mainGroup(this);

    private final IntProperty intProperty1 = new IntProperty("p1", group);
    private final IntProperty intProperty2 = new IntProperty("p2", group);
    private final IntProperty intProperty3 = new IntProperty("p3", group);
    private final IntProperty intProperty4 = new IntProperty("p4", group);
    private final IntProperty intProperty5 = new IntProperty("p5", group);
    private final IntProperty intProperty6 = new IntProperty("p6", group);

    @Test
    void testGroup() {
        final List<Integer> computations = new ArrayList<>();
        new ch.scaille.javabeans.properties.PropertiesAggregator<Integer>("Test", group)
                .addWithMore(intProperty1, intProperty2, intProperty3, intProperty4,
                        (p1, p2, p3, p4, more) ->
                                more.add(intProperty5, intProperty6, (p5, p6) ->
                                        p1.get() + p2.get() + p3.get() +
                                                p4.get() + p5.get() + p6.get()))
                .listenActive(computations::add);
        group.transmitChangesBothWays();
        intProperty1.setValue(this, 10);
        intProperty2.setValue(this, 100);
        intProperty3.setValue(this, 1000);
        intProperty4.setValue(this, 10);
        intProperty5.setValue(this, 100);
        intProperty6.setValue(this, 1000);

        Assertions.assertEquals(List.of(0, 10, 110, 1110, 1120, 1220, 2220), computations);
    }

}
