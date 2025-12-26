package ch.scaille.javabeans;

import ch.scaille.javabeans.chain.Link;
import lombok.Getter;
import org.jspecify.annotations.NullMarked;

import java.util.HashSet;
import java.util.Set;

/**
 * To change the state of the binding chain.
 * <p>
 *
 * @author Sebastien Caille
 */
@NullMarked
public interface IBindingController extends IBindingControl {

	/**
	 * When using makeWeak, an instance of this class must exist and be garbage collected when and only when
	 * the bound component are garbage collected
	 */
	@Getter
	class WeakLinkHolder {
		private final Set<Link<?, ?>> linksHolder = new HashSet<>();
	}

	static WeakLinkHolder weakHolder() {
		return new WeakLinkHolder();
	}


	IBindingController addDependency(IBindingChainDependency dependency);

	IBindingController makeWeak(WeakLinkHolder weakLinkHolder);
}
