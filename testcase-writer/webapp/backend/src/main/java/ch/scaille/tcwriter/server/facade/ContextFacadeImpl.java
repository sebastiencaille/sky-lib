package ch.scaille.tcwriter.server.facade;

import ch.scaille.tcwriter.server.dto.Context;

public class ContextFacadeImpl implements ContextFacade {


	public ContextFacadeImpl() {
	}

	@Override
	public Context merge(Context current, Context newContext) {
		final var dictionary = newContext.getDictionary();
		final var testCase = newContext.getTestCase();

		if (dictionary.isPresent()) {
			current.setDictionary(dictionary.get());
		}
		if (testCase.isPresent()) {
			current.setTestCase(testCase.get());
		}
		return current;
	}

}