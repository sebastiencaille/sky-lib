package ch.skymarshall.dataflowmgr.model;

import java.util.LinkedHashMap;
import java.util.UUID;

public class Call extends WithId {

	private final String name;
	private final String call;
	private final LinkedHashMap<String, String> parameters;
	private final String returnType;

	public Call(final String name, final String methodName, final LinkedHashMap<String, String> parameters,
			final String returnType) {
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

	public LinkedHashMap<String, String> getParameters() {
		return parameters;
	}

	public boolean hasReturnType() {
		return !Void.TYPE.toString().equals(returnType);
	}

}
