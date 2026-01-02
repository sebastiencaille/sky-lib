package ch.scaille.tcwriter.model.dictionary;

import java.util.ArrayList;
import java.util.List;

import ch.scaille.tcwriter.model.IdObject;
import ch.scaille.tcwriter.model.NamedObject;
import lombok.Getter;

/**
 * Factory to create test parameter values
 *
 * @author scaille
 *
 */
@Getter
public class TestParameterFactory extends NamedObject {

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

	public TestParameterFactory(final String id, final String name, final ParameterNature nature, final String type) {
		super(id, name);
		this.parameterType = type;
		this.nature = nature;
	}

    public TestApiParameter getMandatoryParameter(final int index) {
		return mandatoryParameters.get(index);
	}

	public TestApiParameter getMandatoryParameterById(final String id) {
		return mandatoryParameters.stream()
				.filter(p -> p.getId().equals(id))
				.findFirst()
				.orElseThrow(() -> new IllegalStateException("Can't find mandatory parameter " + id));
	}

	public boolean hasMandatoryParameter(final String id) {
		return mandatoryParameters.stream().anyMatch(p -> p.getId().equals(id));
	}

    public TestApiParameter getOptionalParameterById(final String id) {
		return optionalParameters.stream()
				.filter(p -> p.getId().equals(id))
				.findFirst()
				.orElseThrow(() -> new IllegalStateException("Can't find optional parameter " + id));
	}

	public TestApiParameter getOptionalParameterByName(final String name) {
		return optionalParameters.stream()
				.filter(p -> p.getName().equals(name))
				.findFirst()
				.orElseThrow(() -> new IllegalStateException("Can't find optional parameter " + name));
	}

	public boolean hasOptionalParameter(final String id) {
		return optionalParameters.stream().anyMatch(p -> p.getId().equals(id));
	}

    public boolean hasType() {
		return !TestApiParameter.NO_TYPE.equals(parameterType);
	}

	public boolean matches(final TestApiParameter param) {
		return getParameterType().equals(param.getParameterType());

	}

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);
	}
	
	@Override
	public int hashCode() {
		return super.hashCode();
	}
	
	@Override
	public String toString() {
		return String.format("%s, %s, %s mandatory, %s optional ", super.toString(), getParameterType(),
				mandatoryParameters.size(), optionalParameters.size());
	}

	public static TestParameterFactory simpleType(final String type) {
		return new TestParameterFactory("", "", ParameterNature.SIMPLE_TYPE, type);
	}

	public static TestParameterFactory unSet(final TestApiParameter param) {
		return new TestParameterFactory("", "", ParameterNature.NOT_SET, param.getParameterType());
	}

}
