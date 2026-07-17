package ch.scaille.dataflowmgr.model;

import java.util.LinkedHashMap;

public class GenericCall extends Call implements DerivableCall<GenericCall> {

	public GenericCall(final String name, final String methodName, final LinkedHashMap<String, String> parameters,
	                   String returnType) {
		super(name, methodName, parameters, returnType);
	}

	@Override
	public GenericCall derivate(final String to) {
		return new GenericCall(getName(), to + '.' + getCall(), getParameters(), getReturnType());
	}

}
