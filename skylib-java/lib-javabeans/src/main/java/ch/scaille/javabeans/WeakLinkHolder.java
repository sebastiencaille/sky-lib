package ch.scaille.javabeans;

import java.util.HashSet;
import java.util.Set;

import ch.scaille.javabeans.chain.Link;
import lombok.Getter;

/**
 * When using makeWeak, an instance of this class must exist and be garbage collected when and only when
 * the bound component are garbage collected
 */
@Getter
public
class WeakLinkHolder {
	private final Set<Link<?, ?>> linksHolder = new HashSet<>();
}