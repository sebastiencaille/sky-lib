package ch.scaille.tcwriter.server.dto;

import ch.scaille.tcwriter.generators.visitors.HumanReadableVisitor;

public class Context {

	private final Identity identity;
	private String dictionaryName;
	private String testCase;

	public Context() {
		identity = null;
	}

	public Context(Identity identity) {
		this.identity = identity;
	}

	public Identity getIdentity() {
		return identity;
	}

	public String getDictionary() {
		return dictionaryName;
	}

	public void setDictionary(String dictionaryName) {
		this.dictionaryName = dictionaryName;
	}

	public String getTestCase() {
		return testCase;
	}

	public void setTestCase(String testCase) {
		this.testCase = testCase;
	}
	
	public Context derive() {
		var copy = new Context(this.identity);
		copy.setDictionary(dictionaryName);
		copy.setTestCase(testCase);
		return copy;
	}

	@Override
	public String toString() {
		return String.format("[id: %s, dictionary: %s, testCase: %s ]", identity, dictionaryName, testCase);
	}

}
