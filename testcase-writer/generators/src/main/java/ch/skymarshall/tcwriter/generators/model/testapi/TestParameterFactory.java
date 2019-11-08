package ch.skymarshall.tcwriter.generators.model.testapi;

import java.util.ArrayList;
import java.util.List;

import ch.skymarshall.tcwriter.generators.model.IdObject;
import ch.skymarshall.tcwriter.generators.model.NamedObject;

/**
 * Factory to create test parameter values
 *
 * @author scaille
 *
 */
public class TestParameterFactory extends NamedObject {

	public enum ParameterNature {
		SIMPLE_TYPE(true), TEST_API(false), REFERENCE(true), NOT_SET(false);

		private final boolean requiresSimpleValue;

		private ParameterNature(final boolean requiresSimpleValue) {
			this.requiresSimpleValue = requiresSimpleValue;
		}

		public boolean isSimpleValue() {
			return requiresSimpleValue;
		}
	}

	public static final TestParameterFactory NO_FACTORY = new TestParameterFactory(IdObject.ID_NOT_SET,
			IdObject.ID_NOT_SET, ParameterNature.NOT_SET, "");
	private final List<TestApiParameter> mandatoryParameters = new ArrayList<>();
	private final List<TestApiParameter> optionalParameters = new ArrayList<>();
	private final ParameterNature nature;
	private final String parameterType;

	protected TestParameterFactory() {
		super(null, null);
		this.nature = null;
		this.parameterType = null;
	}

	public TestParameterFactory(final String id, final String name, final ParameterNature nature,
			final String type) {
		super(id, name);
		this.parameterType = type;
		this.nature = nature;
	}

	public String getType() {
		return parameterType;
	}

	public ParameterNature getNature() {
		return nature;
	}

	public TestApiParameter getMandatoryParameter(final int index) {
		return mandatoryParameters.get(index);
	}

	public List<TestApiParameter> getMandatoryParameters() {
		return mandatoryParameters;
	}

	public TestApiParameter getOptionalParameter(final String name) {
		return optionalParameters.stream().filter(p -> p.getName().equals(name)).findFirst()
				.orElseThrow(() -> new IllegalStateException("Can't find optional parameter " + name));
	}

	public List<TestApiParameter> getOptionalParameters() {
		return optionalParameters;
	}

	public boolean hasType() {
		return !TestApiParameter.NO_TYPE.equals(parameterType);
	}

	@Override
	public String toString() {
		return super.toString() + ", " + mandatoryParameters.size() + " mandatory, " + optionalParameters.size()
				+ " optional";
	}

	public static TestParameterFactory simpleType(final String type) {
		return new TestParameterFactory("", "", ParameterNature.SIMPLE_TYPE, type);
	}

}
