package ch.scaille.tcwriter.server.facade;

import ch.scaille.tcwriter.server.dao.ContextDao;
import ch.scaille.tcwriter.server.dto.Context;
import ch.scaille.tcwriter.server.dto.Identity;
import ch.scaille.util.helpers.JavaExt.AutoCloseableNoException;

public class ContextFacadeImpl implements ContextFacade {

	private final ThreadLocal<Context> contexts = new InheritableThreadLocal<>();
	private final ContextDao contextDao;

	public ContextFacadeImpl(ContextDao contextDao) {
		this.contextDao = contextDao;
	}

	@Override
	public AutoCloseableNoException set(Context context) {
		contexts.set(context);
		return contexts::remove;
	}

	@Override
	public AutoCloseableNoException load(Identity identity) {
		return set(contextDao.loadContext(identity).derive());
	}

	@Override
	public Context get() {
		return contexts.get();
	}

	@Override
	public Context merge(Context newContext) {
		final var dictionary = newContext.getDictionary();
		final var testCase = newContext.getTestCase();

		final var current = contexts.get();
		if (dictionary.isPresent()) {
			current.setDictionary(dictionary.get());
		}
		if (testCase.isPresent()) {
			current.setTestCase(testCase.get());
		}
		return current;
	}

}
