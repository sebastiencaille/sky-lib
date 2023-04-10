package ch.scaille.tcwriter.server.dto;

import java.util.Optional;

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

	public Optional<String> getDictionary() {
		return Optional.ofNullable(dictionaryName);
	}

	public void setDictionary(String dictionaryName) {
		this.dictionaryName = dictionaryName;
	}

	public Optional<String> getTestCase() {
		return Optional.ofNullable(testCase);
	}

	public void setTestCase(String testCase) {
		this.testCase = testCase;
	}
	
	public Context derive() {
		final var copy = new Context(this.identity);
		copy.setDictionary(dictionaryName);
		copy.setTestCase(testCase);
		return copy;
	}

	@Override
	public String toString() {
		return String.format("[id: %s, dictionary: %s, testCase: %s ]", identity, dictionaryName, testCase);
	}

}
