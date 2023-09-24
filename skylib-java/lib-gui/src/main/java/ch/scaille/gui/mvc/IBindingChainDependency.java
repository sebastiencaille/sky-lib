package ch.scaille.gui.mvc;

public interface IBindingChainDependency {

	void register(IBindingController chain);

	void unbind();
}