package ch.scaille.tcwriter.model.testcase;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import ch.scaille.tcwriter.model.ExportReference;
import ch.scaille.tcwriter.model.dictionary.TestApiParameter;
import ch.scaille.tcwriter.model.dictionary.TestParameterFactory;
import ch.scaille.tcwriter.model.dictionary.TestParameterFactory.ParameterNature;

@JsonIgnoreProperties("factory")
public class ExportableTestParameterValue extends TestParameterValue {

	public static final ExportableTestParameterValue NO_VALUE = new ExportableTestParameterValue("", TestParameterFactory.NO_FACTORY);

	/**
	 * Json
	 */
	public ExportableTestParameterValue() {
		this(null, null, TestParameterFactory.NO_FACTORY, null);
	}
	
	public ExportableTestParameterValue(final TestApiParameter apiParameter, final TestParameterFactory valueFactory) {
		this(UUID.randomUUID().toString(), apiParameter.getId(), valueFactory, null);
	}

	public ExportableTestParameterValue(final TestApiParameter apiParameter, final TestParameterFactory valueFactory,
			final String simpleValue) {
		this(apiParameter.getId(), apiParameter.getId(), valueFactory, simpleValue);
	}

	public ExportableTestParameterValue(final String apiParameterId, final TestParameterFactory valueFactory,
			final String simpleValue) {
		this(apiParameterId, apiParameterId, valueFactory, simpleValue);
	}

	public ExportableTestParameterValue(final String apiParameterId, final TestParameterFactory valueFactory) {
		this(apiParameterId, apiParameterId, valueFactory, null);
	}
	
	public ExportableTestParameterValue(final String id, final String apiParameterId, final TestParameterFactory valueFactory,
			final String simpleValue) {
		super(id, apiParameterId, valueFactory, simpleValue);
	}
	
	@Override
	protected TestParameterValue createTestParameterValue(final String id, final String apiParameterId, final TestParameterFactory valueFactory,
			final String simpleValue) {
		return new ExportableTestParameterValue(id, apiParameterId, valueFactory, simpleValue);
	}
	
	/**
	 * When the object is serialized, save the id of the test parameter factory.
	 * @return
	 */
	@JsonProperty
	public ExportReference getTestParameterFactoryRef() {
		if (factory.getNature() == ParameterNature.SIMPLE_TYPE) {
			return new ExportReference(factory.getNature().name() + ":" + factory.getParameterType());
		}
		return new ExportReference(factory.getId());
	}

	/**
	 * When the object is deserialized, provide how to restore the state of the test parameter factory, according to the saved id
	 * @param ref
	 */
	public void setTestParameterFactoryRef(final ExportReference ref) {
		ref.setRestoreAction((tc, id) -> {
			if (id.isEmpty()) {
				setParameterFactory(TestParameterFactory.NO_FACTORY);
			} else if (id.startsWith(ParameterNature.SIMPLE_TYPE.name())) {
				setParameterFactory(
						TestParameterFactory.simpleType(id.substring(ParameterNature.SIMPLE_TYPE.name().length() + 1)));
			} else {
				setParameterFactory((TestParameterFactory) ((ExportableTestCase) tc).getRestoreValue(id));
			}
		});
	}
}
