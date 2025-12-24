package ch.scaille.util.helpers;

import java.time.Duration;
import java.util.function.Function;

public interface DelayFunction extends Function<Poller, Duration> {
    // inherited
}