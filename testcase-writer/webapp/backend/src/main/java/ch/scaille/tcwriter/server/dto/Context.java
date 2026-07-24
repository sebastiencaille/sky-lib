package ch.scaille.tcwriter.server.dto;

import lombok.Getter;
import lombok.Setter;
import org.jspecify.annotations.Nullable;

import java.io.Serializable;
import java.util.Optional;


@Getter
@Setter
public class Context implements Serializable {

	private @Nullable String dictionaryName;
    private @Nullable String testCase;

	public Optional<String> getDictionaryName() {
		return Optional.ofNullable(dictionaryName);
	}

	public Optional<String> getTestCase() {
		return Optional.ofNullable(testCase);
	}

	@Override
	public String toString() {
		return "[dictionary: %s, testCase: %s ]".formatted(dictionaryName, testCase);
	}

}
