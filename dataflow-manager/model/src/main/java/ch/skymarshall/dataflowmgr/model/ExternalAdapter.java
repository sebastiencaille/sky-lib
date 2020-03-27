package ch.skymarshall.dataflowmgr.model;

import static java.util.Collections.singletonList;

import java.lang.reflect.Method;
import java.util.List;

public class ExternalAdapter extends Call {

	public ExternalAdapter(final String name, final String methodName, final List<String> parameters,
			final String returnType) {
		super(name, methodName, parameters, returnType);
	}

	public ExternalAdapter derivate(final String to) {
		return new ExternalAdapter(getName(), to + '.' + getCall(), getParameters(), getReturnType());
	}

	public static ExternalAdapter from(final Method m) {
		return new ExternalAdapter(methodFullName(m), m.getName(), singletonList(type(m.getParameterTypes()[0])),
				type(m.getReturnType()));
	}

}
