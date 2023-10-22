package ch.scaille.tcwriter.server.dto;

import java.util.Optional;


public class Context {

	private String dictionaryName;
	private String testCase;

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
		final var copy = new Context();
		copy.setDictionary(dictionaryName);
		copy.setTestCase(testCase);
		return copy;
	}

	@Override
	public String toString() {
		return String.format("[dictionary: %s, testCase: %s ]", dictionaryName, testCase);
	}

}
