package ch.scaille.javabeans;

import ch.scaille.javabeans.properties.AbstractProperty;

public interface IBindingControl {
	
	IVetoer getVetoer();

	void forceViewUpdate();

	AbstractProperty getProperty();

	void unbind();
}
