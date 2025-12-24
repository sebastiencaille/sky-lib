package ch.scaille.javabeans;

import org.jspecify.annotations.NullMarked;

/**
 * To change the state of the binding.
 * <p>
 *
 * @author Sebastien Caille
 */
@NullMarked
public interface IBindingController extends IBindingControl {

	IBindingController addDependency(IBindingChainDependency dependency);

}
