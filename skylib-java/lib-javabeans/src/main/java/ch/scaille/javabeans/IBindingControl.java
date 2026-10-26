package ch.scaille.javabeans;

import ch.scaille.javabeans.properties.IProperty;

/**
 * Interface used to control the behavior of the bindings
 */

public interface IBindingControl extends IPropertyController {

	IVetoer getVetoer();

	IProperty getProperty();

	/**
	 * Allows pausing the binding changes until released
	 */
	void pauseBinding();

	/**
	 * Release the binding changes
	 */
	void resumeBinding();
}
