package ch.skymarshall.dataflowmgr.model;

import java.util.LinkedHashMap;

public class Condition extends Call<Condition> {

	public Condition(final String name, final String methodName, final LinkedHashMap<String, String> parameters) {
		super(name, methodName, parameters, Boolean.TYPE.getName());
	}

	@Override
	public Condition derivate(final String to) {
		return new Condition(getName(), to + '.' + getCall(), getParameters());
	}

}
