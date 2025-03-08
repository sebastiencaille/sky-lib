package ch.scaille.javabeans;

public interface IBindingChainDependency {

	void register(IBindingControl chain);

	void unbind();
}