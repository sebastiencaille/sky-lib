package ch.scaille.testing.bdd.definition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import ch.scaille.testing.bdd.definition.ScenarioFragment.Step;

public class Story<P, PP> {

	public static class Context {
		private final Map<Class<?>, Object> context = new HashMap<>();

		public <T> T getContext(Class<T> clazz) {
			return (T) context.computeIfAbsent(clazz, c -> {
				try {
					return c.newInstance();
				} catch (InstantiationException | IllegalAccessException e) {
					throw new IllegalStateException("Unable to create context class " + c.getName(), e);
				}
			});
		}

	}

	public static class StoryContext {
		final List<String> report = new ArrayList<>();

		private void add(String verb, Step<?> step) {
			report.add(verb + step.description.replace("|", "\n  And "));
		}

		public void addGiven(Step<?> step) {
			if (report.isEmpty()) {
				add("Given ", step);
			} else {
				add("  And ", step);
			}
		}

		public void addWhen(Step<?>  step) {
			add(" When ", step);
		}

		public void addThen(Step<?>  step) {
			add(" Then ", step);
		}

		@Override
		public String toString() {
			return String.join("\n",report);
		}
		
	}

	private final ScenarioFragment<P, PP>[] scenarii;
	private Consumer<Context> contextConfigurer;

	public Story(ScenarioFragment<P, PP>... scenarii) {
		this.scenarii = scenarii;
	}

	public Consumer<Context> getContextConfigurer() {
		return contextConfigurer;
	}

	public Story<P, PP> withContext(Consumer<Context> contextConfigurer) {
		this.contextConfigurer = contextConfigurer;
		return this;
	}

	public ScenarioFragment<P, PP>[] getScenarii() {
		return scenarii;
	}
	
	public Story<P, PP> followedBy(ScenarioFragment<P, PP> next) {
		ScenarioFragment<P, PP>[] newScenarii = Arrays.copyOf(getScenarii(), getScenarii().length+1);
		newScenarii[newScenarii.length-1] = next;
		return new Story<>(newScenarii);
	}

	public StoryContext run(P pilot) {
		Context context = new Context();
		if (contextConfigurer != null) {
			contextConfigurer.accept(context);
		}

		ScenarioFragment<P, PP> lastScenario = scenarii[scenarii.length - 1];
		for (ScenarioFragment<P, PP> scenario : scenarii) {
			scenario.run(pilot, context, scenario == lastScenario);
		}
		return context.getContext(StoryContext.class);
	}
	
	public static class ScenarioFactory<P, PP> {

		private final Function<P, PP> pageProviderSupplier;

		public ScenarioFactory(Function<P, PP> pageProviderSupplier) {
			this.pageProviderSupplier = pageProviderSupplier;
		}

		public ScenarioFragment<P, PP> with(Step<PP> when, Step<PP> then) {
			return with(null, when, then);
		}

		public ScenarioFragment<P, PP> with(Step<PP> given, Step<PP> when, Step<PP> then) {
			return new ScenarioFragment<>(pageProviderSupplier, given, when, then);
		}
	}

	public static <P, PP> ScenarioFactory<P, PP> of(Function<P, PP> pageProviderProvider) {
		return new ScenarioFactory<>(pageProviderProvider);
	}

}
