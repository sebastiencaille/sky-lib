package ch.skymarshall.tcwriter.pilot;

public class PollingContext<C> {

	public static PollingContext<Object> generic(PollingContext<?> orig) {
		PollingContext<Object> newContext = new PollingContext<>();
		newContext.setComponent(orig.component, orig.description);
		return newContext;
	}

	public C component;
	public String description;

	public PollingContext() {
	}

	public void setComponent(C component, String description) {
		this.component = component;
		this.description = description;
	}

}