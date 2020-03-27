package ch.skymarshall.dataflowmgr.model;

import java.lang.reflect.Method;
import java.security.InvalidParameterException;
import java.util.List;

public class Condition extends Call {

	public Condition(final String name, final String methodName, final List<String> parameters) {
		super(name, methodName, parameters, Boolean.TYPE.getName());
	}

	public Condition derivate(final String to) {
		return new Condition(getName(), to + '.' + getCall(), getParameters());
	}

	public static Condition from(final Method m) {
		if (!Boolean.TYPE.equals(m.getReturnType())) {
			throw new InvalidParameterException("Condition method must return a boolean:" + m);
		}
		return new Condition(methodFullName(m), m.getName(), parameters(m));
	}

}
