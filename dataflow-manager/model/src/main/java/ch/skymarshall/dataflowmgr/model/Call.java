package ch.skymarshall.dataflowmgr.model;

import java.util.LinkedHashMap;
import java.util.UUID;

public abstract class Call<T extends Call<T>> extends WithId {

	private final String name;
	private final String implCall;
	private final LinkedHashMap<String, String> parameters;
	private final String returnType;

	public abstract T derivate(final String to);

	protected Call(final String name, final String methodName, final LinkedHashMap<String, String> parameters,
			final String returnType) {
		super(UUID.randomUUID());
		this.name = name;
		this.implCall = methodName;
		this.parameters = parameters;
		this.returnType = returnType;
	}

	public String getName() {
		return name;
	}

	public String getCall() {
		return implCall;
	}

	public String getReturnType() {
		return returnType;
	}

	public LinkedHashMap<String, String> getParameters() {
		return parameters;
	}

	public boolean hasReturnType() {
		return !Void.TYPE.toString().equals(returnType);
	}

}
