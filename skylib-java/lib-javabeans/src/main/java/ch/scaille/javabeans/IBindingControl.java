package ch.scaille.javabeans;

import ch.scaille.javabeans.properties.AbstractProperty;

/**
 * Interface used to control the behavior of the bindings
 */
public interface IBindingControl {
	
	IVetoer getVetoer();

	void forceViewUpdate();

	AbstractProperty getProperty();

	void unbind();
}
