package ch.scaille.javabeans;

/**
 * To change the state of the binding.
 * <p>
 *
 * @author Sebastien Caille
 */
public interface IBindingController extends IBindingControl {

	IBindingController addDependency(IBindingChainDependency dependency);

}
