package ch.scaille.javabeans.properties;

import ch.scaille.javabeans.IBindingController;
import ch.scaille.javabeans.PropertyChangeSupportController;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.beans.PropertyChangeSupport;

class PropertiesTest {

    private static class GarbageCollected {

        private final IBindingController.WeakLinkHolder weakLinkHolder = IBindingController.weakHolder();

        private Integer value;

        private GarbageCollected(IntProperty p1) {
            p1.listen(this::useValue).makeWeak(weakLinkHolder);
        }

        private void useValue(Integer value) {
            this.value = value;
        }
    }

    @SuppressWarnings("java:S1854")
    @Test
    void testWeakBinding() {
        final var support = new PropertyChangeSupport(this);
        final var scoped = new PropertyChangeSupportController(support).scoped(this);
        final var p1 = new IntProperty("p1", scoped);

        scoped.transmitChangesBothWays();

        var gcEd = new GarbageCollected(p1);
        p1.setValue(this, 1);
        Assertions.assertEquals(1, gcEd.value);

        for (int i = 0; i < 10; i++) {
            System.gc();
            p1.setValue(this, 2);
            Assertions.assertEquals(2, gcEd.value);
        }

        gcEd = null;
        // Garbage collect the links
        for (int i = 0; i < 10; i++) {
            System.gc();
            p1.setValue(this, 3);
            if (support.getPropertyChangeListeners("p1").length == 0) {
                return;
            }
        }
        Assertions.fail("PropertyChangeListener was not removed");
    }
}
