package ch.skymarshall.dataflowmgr.model;

import java.util.LinkedHashMap;

public class ExternalAdapter extends Call {

	public ExternalAdapter(final String name, final String methodName, final LinkedHashMap<String, String> parameters,
			final String returnType) {
		super(name, methodName, parameters, returnType);
	}

	public ExternalAdapter derivate(final String to) {
		return new ExternalAdapter(getName(), to + '.' + getCall(), getParameters(), getReturnType());
	}

}
