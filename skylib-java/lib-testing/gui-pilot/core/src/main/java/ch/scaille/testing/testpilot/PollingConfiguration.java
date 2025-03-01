package ch.scaille.testing.testpilot;

import java.time.Duration;

import ch.scaille.testing.testpilot.PilotReport.ReportFunction;
import ch.scaille.util.helpers.Poller;

/**
 * A polling of a component
 * 
 * @param <C> type of Component
 */
public interface PollingConfiguration<C> {

	PollingContext<C> getContext();

	Duration getFirstDelay();

	Poller.DelayFunction getDelayFunction();

	ReportFunction<C> getReportFunction();

	String getReportText();

	ActionDelay getActionDelay();

	Duration getTimeout();

}
