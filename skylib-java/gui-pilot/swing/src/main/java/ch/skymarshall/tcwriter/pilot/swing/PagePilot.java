package ch.skymarshall.tcwriter.pilot.swing;

import java.lang.reflect.InvocationTargetException;

import ch.skymarshall.util.dao.metadata.DataObjectManagerFactory;

/**
 * Inspired by the Selenium's Page concept
 * 
 * @author scaille
 *
 */
public class PagePilot {

	private SwingPilot pilot;

	protected PagePilot(SwingPilot pilot) {
		this.pilot = pilot;
	}

	/**
	 * This must be called from the page's constructor
	 */
	protected void initialize() {
		DataObjectManagerFactory.createFor(this).getMetaData().getAttributes().stream() //
				.filter(a -> AbstractSwingComponent.class.isAssignableFrom(a.getType())) //
				.filter(a -> a.getAnnotation(ByName.class) != null)//
				.forEach(a -> {
					String name = a.getAnnotation(ByName.class).value();
					Class<AbstractSwingComponent<?, ?>> pilotClass = ((Class<AbstractSwingComponent<?, ?>>) a
							.getType());
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