package ch.scaille.tcwriter.server.facade;

import java.util.Optional;

import ch.scaille.tcwriter.server.exceptions.DictionaryNotFoundException;
import ch.scaille.tcwriter.server.exceptions.DictionaryNotSetException;
import ch.scaille.tcwriter.server.exceptions.TestCaseNotFoundException;
import ch.scaille.tcwriter.server.exceptions.TestCaseNotSetException;

public class ValidationHelper {

	private ValidationHelper() {
		// noop
	}
	
	public static <T> T validateDictionarySet(Optional<T> dictionary) {
		return dictionary.orElseThrow(DictionaryNotSetException::new);
	}
	
	public static <T> T validateTestCaseSet(Optional<T> tc) {
		return tc.orElseThrow(TestCaseNotSetException::new);
	}

	public static <T> T dictionaryFound(String id,Optional<T> dictionary) {
		return dictionary.orElseThrow(() -> new DictionaryNotFoundException(id));
	}
	
	public static <T> T testCaseFound(String id, Optional<T> tc) {
		return tc.orElseThrow(() -> new TestCaseNotFoundException(id));
	}
	
}
