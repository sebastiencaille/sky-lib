package ch.scaille.testing.bdd.definition;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * 
 * @author scaille
 *
 * @param <P>  Shared type (like Swing/Selenium Pilot)
 * @param <PP> Specific type (like PagePilots)
 */
public abstract class Scenario<P, PP> {

	private final Function<P, PP> pageSupplier;
	private String givenDescription;
	private String whenDescription;
	private String thenDescription;

	protected Scenario(Function<P, PP> pageSupplier, String givenDescription, String whenDescription,
			String thenDescription) {
		this.pageSupplier = pageSupplier;
		this.givenDescription = givenDescription;
		this.whenDescription = whenDescription;
		this.thenDescription = thenDescription;
	}

	public String getWhenCodeDescription() {
		return toBddCodeDescription(whenDescription);
	}

	public String toBddCodeDescription(String descr) {
		return descr.replace(' ', '_').toLowerCase();
	}

	public void run(P pilot, boolean isWhen) {
		PP page = pageSupplier.apply(pilot);
		given(page);
		when(page);
		if (isWhen) {
			then(page);
		}
	}

	public void given(PP page) {
		// noop
	}

	public abstract void when(PP page);

	public abstract void then(PP page);

	public static class ScenarioFactory<P, PP> {

		private final Function<P, PP> pageSupplier;

		public ScenarioFactory(Function<P, PP> pageSupplier) {
			this.pageSupplier = pageSupplier;
		}

		public Scenario<P, PP> with(String whenDescription, Consumer<PP> when, String thenDescription,
				Consumer<PP> then) {
			return with(null, null, whenDescription, when, thenDescription, then);
		}

		public Scenario<P, PP> with(String givenDescription, final Consumer<PP> given, String whenDescription,
				Consumer<PP> when, String thenDescription, Consumer<PP> then) {

			return new Scenario<P, PP>(pageSupplier, givenDescription, whenDescription, thenDescription) {

				@Override
				public void given(PP page) {
					if (given != null) {
						given.accept(page);
					}
				}

				@Override
				public void when(PP page) {
					when.accept(page);
				}

				@Override
				public void then(PP page) {
					then.accept(page);
				}

			};
		}
	}

	public static <P, PP> ScenarioFactory<P, PP> of(Function<P, PP> pageSupplier) {
		return new ScenarioFactory<>(pageSupplier);
	}

}
