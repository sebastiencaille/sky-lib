package org.skymarshall.hmi.mvc;

public interface IBindingChainDependency {

	void register(IBindingController chain);

	void unbind();
}