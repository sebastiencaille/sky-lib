package ch.scaille.tcwriter.model.testcase;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import ch.scaille.tcwriter.model.ExportReference;
import ch.scaille.tcwriter.model.IdObject;
import ch.scaille.tcwriter.model.dictionary.TestDictionary;

@JsonIgnoreProperties({ "testDictionary", "dynamicDescriptions" })
public class ExportableTestCase extends TestCase {

	@JsonIgnore
	private Map<String, IdObject> cachedValues = null;
	
	@JsonIgnore
	private List<ExportReference> references;

	protected String preferredDictionary;
	
	protected ExportableTestCase() {
		super(null, null);
	}

	public ExportableTestCase(String pkgAndClassName, TestDictionary testDictionary) {
		super(pkgAndClassName, testDictionary);
	}

	 public String getPreferredDictionary() {
		return preferredDictionary;
	}
	
	public void setPreferredDictionary(String preferredDictionary) {
		this.preferredDictionary = preferredDictionary;
	}
	
	public void restoreReferences() {
		dynamicDescriptions.putAll(dynamicReferences.values().stream()
				.collect(Collectors.toMap(TestReference::getId, TestReference::toDescription)));
		references.forEach(e -> e.restore(this));
	}

	/**
	 * During restore of value, get the dictionary object matching the id 
	 * @return a TC object (action, ...)
	 */
	public synchronized IdObject getRestoreValue(final String id) {
		if (cachedValues == null) {
			cachedValues = testDictionary.getRoles().values().stream().flatMap(r -> r.getActions().stream())
					.collect(Collectors.toMap(IdObject::getId, a -> a));
			cachedValues.putAll(testDictionary.getTestObjectFactories().values().stream()
					.collect(Collectors.toMap(IdObject::getId, a -> a)));
		}

		var restoredObject = cachedValues.get(id);
		if (restoredObject == null) {
			restoredObject = getReference(id);
		}
		if (restoredObject == null) {
			throw new IllegalArgumentException("No cached value for " + id);
		}
		return restoredObject;
	}

	public void setExportedReferences(List<ExportReference> references) {
		this.references = references;
	}

}
