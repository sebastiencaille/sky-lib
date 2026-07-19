package ch.scaille.dataflowmgr.model;

import java.util.LinkedHashMap;

public class ProcessorCall extends Call implements DerivableCall<ProcessorCall> {

	public ProcessorCall(final String name, final String methodName, final LinkedHashMap<String, String> parameters,
	                     final String returnType) {
		super(name, methodName, parameters, returnType);
	}

	@Override
	public ProcessorCall derivate(final String to) {
		return new ProcessorCall(getName(), to + '.' + getCall(), getParameters(), getReturnType());
	}

	public String asDataPoint() {
		return getCall().replace('.', '_');
	}

}
