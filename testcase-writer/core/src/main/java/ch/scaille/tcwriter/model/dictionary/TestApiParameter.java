package ch.scaille.tcwriter.model.dictionary;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;

import ch.scaille.tcwriter.model.IdObject;
import ch.scaille.tcwriter.model.NamedObject;
import lombok.Getter;

/**
 * A typed parameter of the test action or parameter factory. Used to link the
 * expected type of parameter with the effective type of parameter
 *
 * @author scaille
 *
 */
@Getter
public class TestApiParameter extends NamedObject {

    public static final TestApiParameter NO_PARAMETER = new TestApiParameter(IdObject.ID_NOT_SET, "", "");

    public static final String NO_TYPE = Void.TYPE.getName();

    private final String parameterType;

    protected TestApiParameter() {
        super(null, null);
        parameterType = null;
    }

    @JsonCreator
    public TestApiParameter(final String id, final String name, final String parameterType) {
        super(id, name);
        this.parameterType = parameterType;
    }

    public boolean hasType() {
        return !TestApiParameter.NO_TYPE.equals(parameterType);
    }

    @JsonIgnore
    public TestParameterFactory asSimpleParameter() {
        return TestParameterFactory.simpleType(getParameterType());
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
        return getName() + ": " + parameterType;
    }


}
