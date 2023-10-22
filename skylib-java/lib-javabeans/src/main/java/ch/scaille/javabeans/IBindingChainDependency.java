package ch.scaille.javabeans;

public interface IBindingChainDependency {

	void register(IBindingController chain);

	void unbind();
}