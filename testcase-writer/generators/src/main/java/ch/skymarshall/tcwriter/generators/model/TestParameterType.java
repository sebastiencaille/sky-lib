package ch.skymarshall.tcwriter.generators.model;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class TestParameterType extends IdObject {

	public static final TestParameterType NO_VALUE = new TestParameterType(IdObject.ID_NOT_SET, "", "");

	private final String type;
	private String name;

	public TestParameterType(final String id, final String name, final String type) {
		super(id);
		this.name = name;
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}

	@Override
	public String toString() {
		return getName() + ": " + type;
	}

	public TestParameter asParameter() {
		return TestParameter.simpleType(getType());
	}

	protected void setName(final String newName) {
		this.name = newName;
	}

	private static final Set<String> SIMPLE_TYPES = Arrays
			.asList(String.class, Integer.TYPE, Integer.class, Long.TYPE, Long.class).stream().map(c -> c.getName())
			.collect(Collectors.toSet());

	public boolean isSimpleType() {
		return SIMPLE_TYPES.contains(getType());
	}
}
