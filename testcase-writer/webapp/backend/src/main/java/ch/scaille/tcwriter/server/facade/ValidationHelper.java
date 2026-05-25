package ch.scaille.tcwriter.server.facade;

import ch.scaille.tcwriter.model.Metadata;
import ch.scaille.tcwriter.server.exceptions.DictionaryNotFoundException;
import ch.scaille.tcwriter.server.exceptions.TestCaseNotFoundException;
import org.jspecify.annotations.Nullable;

public class ValidationHelper {

	private ValidationHelper() {
		// noop
	}

	public static Metadata dictionaryFound(String id, @Nullable Metadata dictionary) {
		if (dictionary == null) {
			throw new DictionaryNotFoundException(id);
		}
		return dictionary;
	}

	public static <T> T dictionaryFound(String id, @Nullable T dictionary) {
		if (dictionary == null) {
			throw new DictionaryNotFoundException(id);
		}
		return dictionary;
	}

	public static <T> T testCaseFound(String id, @Nullable T tc) {
		if (tc == null) {
			throw  new TestCaseNotFoundException(id);
		}
		return tc;
	}

	public static Metadata testCaseFound(String id, @Nullable Metadata tc) {
		if (tc == null) {
			throw  new TestCaseNotFoundException(id);
		}
		return tc;
	}


}
