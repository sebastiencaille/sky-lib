package ch.skymarshall.dataflowmgr.model;

import java.lang.reflect.Method;

public class Processor {

	private final String name;
	private final String call;
	private final String parameter;
	private final String returnType;

	public Processor(final String name, final String methodName, final String parameter, final String returnType) {
		this.name = name;
		this.call = methodName;
		this.parameter = parameter;
		this.returnType = returnType;
	}

	public String getName() {
		return name;
	}

	public String getCall() {
		return call;
	}

	public String getReturnType() {
		return returnType;
	}

	public String getParameter() {
		return parameter;
	}

	public Processor derivate(final String to) {
		return new Processor(to, to + '.' + call, parameter, returnType);
	}

	public static Processor from(final Method m) {
		return new Processor(m.getDeclaringClass().getName() + "." + m.getName(), m.getName(),
				m.getParameterTypes()[0].getName(), m.getReturnType().getName());
	}

}
