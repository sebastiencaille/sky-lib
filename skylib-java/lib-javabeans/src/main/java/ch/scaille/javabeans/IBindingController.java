package ch.scaille.javabeans;

/**
 * To change the state of the binding chain.
 * <p>
 *
 * @author Sebastien Caille
 */

public interface IBindingController extends IBindingControl {

	static WeakLinkHolder weakHolder() {
		return new WeakLinkHolder();
	}


	IBindingController addDependency(IBindingChainDependency dependency);

	IBindingController makeWeak(WeakLinkHolder weakLinkHolder);
}
