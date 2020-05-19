package ch.skymarshall.dataflowmgr.model;

import java.util.LinkedHashMap;

public class Processor extends Call<Processor> {

	public Processor(final String name, final String methodName, final LinkedHashMap<String, String> parameters,
			final String returnType) {
		super(name, methodName, parameters, returnType);
	}

	@Override
	public Processor derivate(final String to) {
		return new Processor(getName(), to + '.' + getCall(), getParameters(), getReturnType());
	}

	public String asDataPoint() {
		return getCall().replace('.', '_');
	}

}
