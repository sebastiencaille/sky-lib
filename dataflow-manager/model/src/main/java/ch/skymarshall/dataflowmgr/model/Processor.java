package ch.skymarshall.dataflowmgr.model;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Processor extends Call {

	public Processor(final String name, final String methodName, final List<String> parameters,
			final String returnType) {
		super(name, methodName, parameters, returnType);
	}

	public Processor derivate(final String to) {
		return new Processor(getName(), to + '.' + getCall(), getParameters(), getReturnType());
	}

	public static Processor from(final Method m) {
		return new Processor(methodFullName(m), m.getName(),
				Arrays.stream(m.getParameterTypes()).map(Call::type).collect(Collectors.toList()),
				type(m.getReturnType()));
	}

	public String asDataPoint() {
		return getCall().replace('.', '_');
	}

}
