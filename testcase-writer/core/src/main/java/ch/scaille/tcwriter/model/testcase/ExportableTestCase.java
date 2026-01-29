package ch.scaille.tcwriter.model.testcase;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import ch.scaille.tcwriter.mappers.Default;
import ch.scaille.tcwriter.model.Metadata;
import ch.scaille.tcwriter.model.TestObjectDescription;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import ch.scaille.tcwriter.model.ExportReference;
import ch.scaille.tcwriter.model.IdObject;
import ch.scaille.tcwriter.model.dictionary.TestDictionary;
import com.google.common.collect.Multimap;
import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties({ "testDictionary", "dynamicDescriptions" })
public class ExportableTestCase extends TestCase {

	@JsonIgnore
	private Map<String, IdObject> cachedValues = null;
	
	@JsonIgnore
	private List<ExportReference> references;

	@Setter
    @Getter
    protected String preferredDictionary;
	
	protected ExportableTestCase() {
		super("", new TestDictionary());
	}

	public ExportableTestCase(String pkgAndClassName, TestDictionary testDictionary) {
		super(pkgAndClassName, testDictionary);
	}

	@Default
	@JsonCreator
	public ExportableTestCase(Metadata metadata, List<TestStep> steps, final String pkgAndClassName,
					Multimap<String, TestReference> dynamicReferences,
					Map<String, TestObjectDescription> dynamicDescriptions) {
		super(metadata, steps, pkgAndClassName, dynamicReferences, dynamicDescriptions);
	}

    public void restoreReferences() {
		references.forEach(e -> e.restore(this));
		dynamicDescriptions.putAll(dynamicReferences.values().stream()
				.collect(Collectors.toMap(TestReference::getId, TestReference::toDescription)));
		cachedValues = null;
	}

	/**
	 * During restore of value, get the dictionary object matching the id 
	 * @return a TC object (action, ...)
	 */
	public synchronized IdObject getRestoreValue(final String id) {
		if (cachedValues == null) {
			cachedValues = Objects.requireNonNull(testDictionary, "No dictionary set").getRoles().values().stream().flatMap(r -> r.getActions().stream())
					.collect(Collectors.toMap(IdObject::getId, a -> a));
			cachedValues.putAll(testDictionary.getTestObjectFactories().values().stream()
					.collect(Collectors.toMap(IdObject::getId, a -> a)));
			cachedValues.putAll(getSteps().stream().map(TestStep::getReference)
					.filter(Objects::nonNull)
					.collect(Collectors.toMap(IdObject::getId, a -> a)));
		}

		var restoredObject = cachedValues.get(id);
		if (restoredObject == null) {
			restoredObject = getReference(id)
				.orElseThrow(() -> new IllegalArgumentException("Unable to find value to restore [" + id + ']'));
		}
		return restoredObject;
	}

	public void setExportedReferences(List<ExportReference> references) {
		this.references = references;
	}

}
