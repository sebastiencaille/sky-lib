package ch.scaille.tcwriter.server.webapi.controllers;

import java.util.Optional;

import ch.scaille.tcwriter.server.webapi.controllers.exceptions.DictionaryNotSetException;
import ch.scaille.tcwriter.server.webapi.controllers.exceptions.TestCaseNotSetException;

public class ControllerHelper {

	private ControllerHelper() {
		// noop
	}
	
	public static <T> T validateDictionarySet(Optional<T> dictionary) {
		return dictionary.orElseThrow(DictionaryNotSetException::new);
	}
	
	public static <T> T validateTestCaseSet(Optional<T> tc) {
		return tc.orElseThrow(TestCaseNotSetException::new);
	}
	
}
