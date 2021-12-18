package ch.skymarshall.tcwriter.pilot;

import ch.skymarshall.util.helpers.Poller;

public class PollingContext<C> {

	public static PollingContext<Object> generic(PollingContext<?> orig) {
		PollingContext<Object> newContext = new PollingContext<>(orig.poller);
		newContext.setComponent(orig.component, orig.description);
		return newContext;
	}

	public final Poller poller;

	public C component;
	public String description;

	public PollingContext(Poller poller) {
		this.poller = poller;
	}

	public void setComponent(C component, String description) {
		this.component = component;
		this.description = description;
	}

}