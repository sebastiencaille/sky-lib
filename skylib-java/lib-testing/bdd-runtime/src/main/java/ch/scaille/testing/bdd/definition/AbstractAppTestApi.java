package ch.scaille.testing.bdd.definition;

import java.util.function.Supplier;

public abstract class AbstractAppTestApi<C> {

	private final Supplier<C> contextFactory;

	private C context;

	protected AbstractAppTestApi(Supplier<C> contextFactory) {
		this.contextFactory = contextFactory;
	}

	public C getContext() {
		return context;
	}

	public void resetContext() {
		context = contextFactory.get();
	}

}
