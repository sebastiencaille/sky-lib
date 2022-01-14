package ch.scaille.tcwriter.pilot.selenium.bdd;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.function.Consumer;
import java.util.function.Function;

import ch.scaille.tcwriter.pilot.selenium.PagePilot;
import ch.scaille.tcwriter.pilot.selenium.SeleniumPilot;

public abstract class Scenario<T extends PagePilot> {

	private Function<SeleniumPilot, T> pageSupplier;

	public Scenario(Function<SeleniumPilot, T> pageSupplier) {
		this.pageSupplier = pageSupplier;
	}

	protected String findName(T page) {
		for (Field f : page.getClass().getDeclaredFields()) {
			try {
				if (Modifier.isStatic(f.getModifiers()) && f.get(null) == this) {
					return f.getName();
				}
			} catch (IllegalArgumentException | IllegalAccessException e) {
				// ignore
			}
		}
		return null;
	}

	public String getName(SeleniumPilot pilot) {
		return findName(pageSupplier.apply(pilot));
	}

	public void run(SeleniumPilot pilot, boolean isWhen) {
		T page = pageSupplier.apply(pilot);
		given(page);
		when(page);
		if (isWhen) {
			then(page);
		}
	}

	public void given(T page) {
		// noop
	}

	public abstract void when(T page);

	public abstract void then(T page);

	public static class ScenarioFactory<T extends PagePilot> {

		private final Function<SeleniumPilot, T> pageSupplier;

		public ScenarioFactory(Function<SeleniumPilot, T> pageSupplie) {
			this.pageSupplier = pageSupplie;
		}

		public Scenario<T> with(Consumer<T> when, Consumer<T> then) {
			return with(null, when, then);
		}

		public Scenario<T> with(final Consumer<T> given, final Consumer<T> when, final Consumer<T> then) {
			return new Scenario<T>(pageSupplier) {

				@Override
				public void given(T page) {
					if (given != null) {
						given.accept(page);
					}
				}

				@Override
				public void when(T page) {
					when.accept(page);
				}

				@Override
				public void then(T page) {
					then.accept(page);
				}

			};
		}
	}

	public static <T extends PagePilot> ScenarioFactory<T> of(Function<SeleniumPilot, T> pageSupplier) {
		return new ScenarioFactory<>(pageSupplier);
	}

}
