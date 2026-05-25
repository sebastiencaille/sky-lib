package ch.scaille.tcwriter.server.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;


@Getter
@Setter
public class Context implements Serializable {

	private String dictionaryName;
    private String testCase;

	public Optional<String> getDictionaryName() {
		return Optional.ofNullable(dictionaryName);
	}

	public Optional<String> getTestCase() {
		return Optional.ofNullable(testCase);
	}

    public Context copy() {
		final var copy = new Context();
		copy.setDictionaryName(dictionaryName);
		copy.setTestCase(testCase);
		return copy;
	}

	public boolean differs(Context other) {
		return Objects.equals(dictionaryName, other.dictionaryName) && Objects.equals(testCase, other.testCase);
	}
	
	@Override
	public String toString() {
		return String.format("[dictionary: %s, testCase: %s ]", dictionaryName, testCase);
	}

}
