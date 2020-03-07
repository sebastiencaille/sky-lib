package ch.skymarshall.dataflowmgr.model;

import java.lang.reflect.Method;

public class ExternalInput {

	private final String name;
	private final String call;
	private final String dataType;
	private final String queryInput;

	public ExternalInput(final String name, final String methodCall, final String dataType, final String queryInput) {
		this.name = name;
		this.call = methodCall;
		this.dataType = dataType;
		this.queryInput = queryInput;
	}

	public String getName() {
		return name;
	}

	public ExternalInput derivate(final String to) {
		return new ExternalInput(name, to + '.' + call, dataType, queryInput);
	}

	public static ExternalInput from(final Method m) {
		return new ExternalInput(m.getName(), m.getName(), m.getReturnType().getName(),
				m.getParameterTypes()[0].getName());
	}

}
