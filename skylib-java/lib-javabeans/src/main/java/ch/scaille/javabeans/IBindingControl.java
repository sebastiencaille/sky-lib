package ch.scaille.javabeans;

import ch.scaille.javabeans.properties.AbstractProperty;

public interface IBindingControl {
	IVeto getVetoer();

	void forceViewUpdate();

	AbstractProperty getProperty();

	void unbind();
}
