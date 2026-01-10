package ch.scaille.testing.bdd.definition;

import lombok.Getter;

import java.util.function.Supplier;

@Getter
public abstract class AbstractAppTestApi<C> {

	private final Supplier<C> contextFactory;

    private C context;

	protected AbstractAppTestApi(Supplier<C> contextFactory) {
		this.contextFactory = contextFactory;
	}

    public void resetContext() {
		context = contextFactory.get();
	}

}
