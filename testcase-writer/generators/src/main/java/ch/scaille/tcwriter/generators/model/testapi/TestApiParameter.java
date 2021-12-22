package ch.scaille.tcwriter.generators.model.testapi;

import com.fasterxml.jackson.annotation.JsonIgnore;

import ch.scaille.tcwriter.generators.model.IdObject;
import ch.scaille.tcwriter.generators.model.NamedObject;

/**
 * A typed parameter of the test action or parameter factory. Used to link the
 * expected type of parameter with the effective type of parameter
 *
 * @author scaille
 *
 */
public class TestApiParameter extends NamedObject {

	public static final TestApiParameter NO_PARAMETER = new TestApiParameter(IdObject.ID_NOT_SET, "", "");

	public static final String NO_TYPE = Void.TYPE.getName();

	private final String parameterType;

	protected TestApiParameter() {
		super(null, null);
		parameterType = null;
	}

	public TestApiParameter(final String id, final String name, final String type) {
		super(id, name);
		this.parameterType = type;
	}

	public String getType() {
		return parameterType;
	}

	public boolean hasType() {
		return !TestApiParameter.NO_TYPE.equals(parameterType);
	}

	@Override
	public String toString() {
		return getName() + ": " + parameterType;
	}

	@JsonIgnore
	public TestParameterFactory asSimpleParameter() {
		return TestParameterFactory.simpleType(getType());
	}

}
