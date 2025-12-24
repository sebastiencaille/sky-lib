package ch.scaille.javabeans;

import org.jspecify.annotations.NullMarked;

@NullMarked
public interface IBindingChainDependency {

	void register(IBindingControl chain);

	void unbind();
}