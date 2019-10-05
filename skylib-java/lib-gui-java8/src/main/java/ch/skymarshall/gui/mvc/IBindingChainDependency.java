package ch.skymarshall.gui.mvc;

public interface IBindingChainDependency {

	void register(IBindingController chain);

	void unbind();
}