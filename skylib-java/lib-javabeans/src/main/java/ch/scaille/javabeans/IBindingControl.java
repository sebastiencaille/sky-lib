package ch.scaille.javabeans;

import ch.scaille.javabeans.properties.AbstractProperty;

/**
 * Interface used to control the behavior of the bindings
 */
public interface IBindingControl extends IPropertyController {
	
	IVetoer getVetoer();

	AbstractProperty getProperty();

	
	/**
	 * Bufferize the binding changes until all release are called
	 */
	void bufferizeBinding();

	/**
	 * Release the binding changes
	 */
	void releaseBinding();
}
