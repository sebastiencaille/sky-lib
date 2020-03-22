package ch.skymarshall.dataflowmgr.model;

import java.lang.reflect.Method;
import java.util.List;
import java.util.UUID;

public class Call extends WithId {

	private final String name;
	private final String call;
	private final List<String> parameters;
	private final String returnType;

	public Call(final String name, final String methodName, final List<String> parameters, final String returnType) {
		super(UUID.randomUUID());
		this.name = name;
		this.call = methodName;
		this.parameters = parameters;
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

	public List<String> getParameters() {
		return parameters;
	}

	public boolean hasReturnType() {
		return !Void.TYPE.toString().equals(returnType);
	}

	public static String methodFullName(final Method m) {
		return m.getDeclaringClass().getName() + "." + m.getName();
	}

	public static String type(final Class<?> c) {
		return c.getName().replace('$', '.');
	}

}
