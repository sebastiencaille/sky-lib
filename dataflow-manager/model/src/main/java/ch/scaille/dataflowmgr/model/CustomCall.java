package ch.scaille.dataflowmgr.model;

import java.util.LinkedHashMap;

public class CustomCall extends Call<CustomCall> {

	public CustomCall(final String name, final String methodName, final LinkedHashMap<String, String> parameters,
			String returnType) {
		super(name, methodName, parameters, returnType);
	}

	@Override
	public CustomCall derivate(final String to) {
		return new CustomCall(getName(), to + '.' + getCall(), getParameters(), getReturnType());
	}

}
