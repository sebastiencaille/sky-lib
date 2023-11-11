package ch.scaille.gui.mvc;

import ch.scaille.javabeans.PropertyChangeSupportController;
import ch.scaille.javabeans.IPropertiesGroup;

/**
 * Base of MVC controller.
 * <p>
 * This class gives access of basic components used by the controller
 *
 * @author Sebastien Caille
 *
 */
public class GuiController {

	/**
	 * The main properties group
	 */
	protected final IPropertiesGroup propertySupport;

	public GuiController() {
		this.propertySupport = PropertyChangeSupportController.mainGroup(this);
	}

	public GuiController(final PropertyChangeSupportController propertySupport) {
		this.propertySupport = propertySupport.scoped(this);
	}

	public GuiController(final IPropertiesGroup propertySupport) {
		this.propertySupport = propertySupport;
	}

	public IPropertiesGroup getScopedChangeSupport() {
		return propertySupport;
	}

	/**
	 * Starts the controller, to be called once once all the GUIs component are
	 * bound to the controller. It actually attaches all the properties, causing the
	 * values to be sent to the components
	 */
	public void activate() {
		propertySupport.attachAll();
	}

}
