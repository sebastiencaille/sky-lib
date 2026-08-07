package ch.scaille.testing.bdd.definition;

import lombok.Getter;
import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.function.Supplier;

@Getter
public abstract class AbstractAppTestApi<C> {

	private final Supplier<C> contextFactory;

	@Nullable
    private C context;

	protected AbstractAppTestApi(Supplier<C> contextFactory) {
		this.contextFactory = contextFactory;
	}

    public void resetContext() {
		context = contextFactory.get();
	}

	public C getContext() {
		return Objects.requireNonNull(context, "Context was never reset");
	}

}
