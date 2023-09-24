package ch.scaille.tcwriter.pilot;

public class PollingContext<C> {

	public static PollingContext<Object> generic(PollingContext<?> orig) {
		final var newContext = new PollingContext<>();
		newContext.setComponent(orig.component, orig.description);
		return newContext;
	}

	public C component;
	public String description;

	public PollingContext() {
		// noop
	}

	public void setComponent(C component, String description) {
		this.component = component;
		this.description = description;
	}

}