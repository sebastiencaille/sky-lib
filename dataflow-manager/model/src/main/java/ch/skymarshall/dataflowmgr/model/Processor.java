package ch.skymarshall.dataflowmgr.model;

import java.lang.reflect.Method;
import java.util.List;

public class Processor extends Call {

	public Processor(final String name, final String methodName, final List<String> parameters,
			final String returnType) {
		super(name, methodName, parameters, returnType);
	}

	public Processor derivate(final String to) {
		return new Processor(getName(), to + '.' + getCall(), getParameters(), getReturnType());
	}

	public static Processor from(final Method m) {
		return new Processor(methodFullName(m), m.getName(), parameters(m), type(m.getReturnType()));
	}

	public String asDataPoint() {
		return getCall().replace('.', '_');
	}

}
