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
	 * This must be called from the page's constructor
	 */
	protected void initialize() {
		DataObjectManagerFactory.createFor(this)
				.getMetaData()
				.getAttributes()
				.stream() //
				.filter(a -> AbstractSwingComponentPilot.class.isAssignableFrom(a.getType())) //
				.filter(a -> a.getAnnotation(ByName.class).isPresent())//
				.forEach(a -> {
					final var name = a.getAnnotation(ByName.class).get().value();
					final var pilotClass = ((Class<AbstractSwingComponentPilot<?>>) a.getType());
					try {
						a.setValueOf(this,
								pilotClass.getConstructor(SwingPilot.class, String.class).newInstance(pilot, name));
					} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
							| InvocationTargetException | NoSuchMethodException | SecurityException e) {
						throw new IllegalStateException("Cannot inject Swing component pilot", e);
					}
				});
	}

}
