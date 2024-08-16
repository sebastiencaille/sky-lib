package ch.scaille.tcwriter.pilot;

import java.time.Duration;

import ch.scaille.tcwriter.pilot.PilotReport.ReportFunction;
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
