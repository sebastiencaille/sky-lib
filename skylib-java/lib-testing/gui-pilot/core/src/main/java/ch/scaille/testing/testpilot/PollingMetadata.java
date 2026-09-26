package ch.scaille.testing.testpilot;

import java.time.Duration;
import java.util.Optional;

import ch.scaille.testing.testpilot.PilotReport.PilotReportBuilder;
import ch.scaille.util.helpers.DelayFunction;
import org.jspecify.annotations.Nullable;

/**
 * A polling of a component
 * 
 * @param <C> type of Component
 */

public interface PollingMetadata<C> {

	Optional<PolledComponent<C>> getComponent();

	DelayFunction getDelayFunction();

	PilotReportBuilder<C> getReportBuilder();

	@Nullable
	ActionDelay getAndThen();

	Duration getTimeout();

	AbstractComponentPilot<C> getComponentPilot();

	default <T extends AbstractComponentPilot<C>> T getComponentPilot(Class<T> clazz) {
		return clazz.cast(getComponentPilot());
	}

	default GuiPilot getGuiPilot() {
		return getComponentPilot().getPilot();
	}

	default <T extends GuiPilot> T getGuiPilot(Class<T> clazz) {
		return clazz.cast(getGuiPilot());
	}

}
