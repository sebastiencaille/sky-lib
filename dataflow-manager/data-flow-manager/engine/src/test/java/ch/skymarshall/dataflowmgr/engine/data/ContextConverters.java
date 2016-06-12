package ch.skymarshall.dataflowmgr.engine.data;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class ContextConverters {

	private static <T> void setIfNotNull(final Supplier<T> supplier, final Consumer<T> consumer) {
		final T value = supplier.get();
		if (value != null) {
			consumer.accept(value);
		}
	}

	public static GreetingsCtxt copy(final GreetingsCtxt orig) {
		final GreetingsCtxt newCtxt = new GreetingsCtxt(orig.getMood());
		newCtxt.setGreetings(orig.getGreetings());
		newCtxt.setResult(orig.getResult());
		return newCtxt;
	}

	public static void reIntegrate(final GreetingsCtxt copy, final GreetingsCtxt main) {
		setIfNotNull(copy::getMood, main::setMood);
		setIfNotNull(copy::getGreetings, main::setGreetings);
		setIfNotNull(copy::getResult, main::setResult);
	}

}
