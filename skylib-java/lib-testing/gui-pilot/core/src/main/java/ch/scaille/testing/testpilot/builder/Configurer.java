package ch.scaille.testing.testpilot.builder;

import ch.scaille.testing.testpilot.Polling;

import java.time.Duration;
import java.util.function.Consumer;

/**
 * This allows extending the configuration
 */
public class Configurer<C, V> {

    private final AssertionBuilder<C, ?, ?, ?> assertionBuilder;

    public Configurer(AssertionBuilder<C, ?, ?, ?> assertionBuilder) {
        this.assertionBuilder = assertionBuilder;
    }

    public V withConfig(Consumer<Polling.PollingBuilder<C, ?>> configurer) {
        assertionBuilder.configurers.add(configurer);
        return (V) this;
    }

    public V timingOutAfter(Duration timeout) {
        withConfig(polling -> polling.timeout(timeout));
        return (V) this;
    }

}