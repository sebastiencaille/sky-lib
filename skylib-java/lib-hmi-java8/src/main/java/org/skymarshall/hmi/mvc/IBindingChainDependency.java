package org.skymarshall.hmi.mvc;

public interface IBindingChainDependency {

	void register(BindingChain chain);

	void unbind();
}