package ch.scaille.tcwriter.pilot;

/**
 * Context of the polling, giving access to meta-information of the polling
 * (like, the component, some descriptions, ...)
 * 
 * @param <C>
 */
public class PollingContext<C> {

	public static PollingContext<Object> generic(PollingContext<?> orig) {
		final var newContext = new PollingContext<>((AbstractComponentPilot<?, Object>) orig.pilot);
		newContext.setComponent(orig.component, orig.description);
		return newContext;
	}

	private final AbstractComponentPilot<?, C> pilot;
	private C component;
	private String description;

	public PollingContext(AbstractComponentPilot<?, C> pilot) {
		this.pilot = pilot;
	}

	public AbstractComponentPilot<?, C> getPilot() {
		return pilot;
	}
	
	public GuiPilot getGuiPilot() {
		return pilot.getPilot();
	}

	public C getComponent() {
		return component;
	}

	public String getDescription() {
		return description;
	}

	public void setComponent(C component, String description) {
		this.component = component;
		this.description = description;
	}
}