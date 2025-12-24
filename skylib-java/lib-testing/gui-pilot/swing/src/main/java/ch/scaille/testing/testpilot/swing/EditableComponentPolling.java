package ch.scaille.testing.testpilot.swing;

import java.util.function.Predicate;

import javax.swing.JComponent;

import ch.scaille.testing.testpilot.Polling;
import ch.scaille.testing.testpilot.PolledComponent;

public class EditableComponentPolling {

    public static <V> Polling.PollingBuilder<JComponent, V> ofComponent(Polling.PollingFunction<JComponent, V> function) {
        return Polling.of(function);
    }

    public static <V> Polling.PollingBuilder<JComponent, V> ofComponent(final Predicate<PolledComponent<JComponent>> precondition, Polling.PollingFunction<JComponent, V> function) {
        return Polling.of(precondition.and(context -> context.getComponentPilot(SwingComponentPilot.class).canEdit(context)),
                function);
    }

}
