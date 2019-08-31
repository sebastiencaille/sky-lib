package ch.skymarshall.tcwriter.generators.model.testapi;

import com.fasterxml.jackson.annotation.JsonIgnore;

import ch.skymarshall.tcwriter.generators.model.IdObject;
import ch.skymarshall.tcwriter.generators.model.NamedObject;

/**
 * A typed parameter of the test action or parameter factory. Used to link the
 * expected type of parameter with the effective type of parameter
 *
 * @author scaille
 *
 */
public class TestParameterType extends NamedObject {

	public static final TestParameterType NO_PARAMETER_TYPE = new TestParameterType(IdObject.ID_NOT_SET, "", "");

	private final String parameterType;

	protected TestParameterType() {
		super(null, null);
		parameterType = null;
	}

	public TestParameterType(final String id, final String name, final String type) {
		super(id, name);
		this.parameterType = type;
	}

	public String getType() {
		return parameterType;
	}

	@Override
	public String toString() {
		return getName() + ": " + parameterType;
	}

	@JsonIgnore
	public TestParameterDefinition asSimpleParameter() {
		return TestParameterDefinition.simpleType(getType());
	}

}
