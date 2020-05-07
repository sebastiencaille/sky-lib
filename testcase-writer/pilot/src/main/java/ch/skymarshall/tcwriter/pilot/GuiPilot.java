package ch.skymarshall.tcwriter.pilot;

import java.time.Duration;

public class GuiPilot {

	private ActionDelay actionDelay;

	private Duration defaultActionTimeout = Duration.ofSeconds(30);

	public void setActionDelay(final ActionDelay actionDelay) {
		this.actionDelay = actionDelay;
	}

	public ActionDelay getActionDelay() {
		return actionDelay;
	}

	public void setDefaultActionTimeout(final Duration defaultActionTimeout) {
		this.defaultActionTimeout = defaultActionTimeout;
	}

	public Duration getDefaultActionTimeout() {
		return defaultActionTimeout;
	}

}
