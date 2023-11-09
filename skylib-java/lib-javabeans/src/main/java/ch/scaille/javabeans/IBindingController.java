package ch.scaille.javabeans;

import ch.scaille.javabeans.properties.AbstractProperty;

/**
 * To change the state of the binding.
 * <p>
 *
 * @author Sebastien Caille
 *
 * @param <ComponentType>
 */
public interface IBindingController {

	IVeto getVeto();

	void forceViewUpdate();

	AbstractProperty getProperty();

	void unbind();

	IBindingController addDependency(IBindingChainDependency dependency);

}
