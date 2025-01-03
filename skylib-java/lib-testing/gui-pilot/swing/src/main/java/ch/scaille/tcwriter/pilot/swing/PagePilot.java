package ch.scaille.tcwriter.pilot.swing;

import java.lang.reflect.InvocationTargetException;

import ch.scaille.util.dao.metadata.DataObjectManagerFactory;

/**
 * Inspired by the Selenium's Page concept
 * 
 * @author scaille
 *
 */
public class PagePilot {

	private final SwingPilot pilot;

	protected PagePilot(SwingPilot pilot) {
		this.pilot = pilot;
	}

	/**
	 * Injects the pilots in the page.
	 * This must be called from the page's constructor
	 */
	protected void initialize() {
		DataObjectManagerFactory.createFor(this)
				.getMetaData()
				.getAttributes()
				.stream() //
				.filter(a -> a.isOfType(SwingComponentPilot.class) || a.isOfType(SwingPollingBuilder.class)) //
				.map(a -> a.unwrap(Object.class))
				.forEach(a -> a.getAnnotation(ByName.class).ifPresent(v -> {
						final var name = v.value();
						final var pilotClass = a.getType();
						try {
							a.setValueOf(this,
									pilotClass.getConstructor(SwingPilot.class, String.class).newInstance(pilot, name));
						} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
								 | InvocationTargetException | NoSuchMethodException | SecurityException e) {
							throw new IllegalStateException("Cannot inject Swing component pilot", e);
						}
					})
				);
	}

}
