package ch.scaille.testing.testpilot;

import org.jspecify.annotations.NullMarked;

/**
 * Context of the polling, giving access to meta-information of the polling
 * (i.e. the component, some descriptions, ...)
 *
 * @param <C>
 */
@NullMarked
public record PolledComponent<C>(AbstractComponentPilot<C> componentPilot,
                                 C component,
                                 String description) {

    public static PolledComponent<Object> generic(PolledComponent<?> orig) {
        return new PolledComponent<>((AbstractComponentPilot<Object>) orig.componentPilot, orig.component, orig.description);
    }

    public <T extends AbstractComponentPilot<C>> T getComponentPilot(Class<T> clazz) {
        return clazz.cast(componentPilot());
    }

    public <T extends GuiPilot> T getGuiPilot(Class<T> clazz) {
        return clazz.cast(componentPilot().getPilot());
    }
}