package ch.scaille.testing.bdd.definition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import ch.scaille.testing.bdd.definition.Scenario.Step;

public class Story<P> {

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

	private final Scenario<P, ?>[] scenarii;
	private Consumer<Context> contextConfigurer;

	public Story(Scenario<P, ?>... scenarii) {
		this.scenarii = scenarii;
	}

	public Consumer<Context> getContextConfigurer() {
		return contextConfigurer;
	}

	public Story<P> withContext(Consumer<Context> contextConfigurer) {
		this.contextConfigurer = contextConfigurer;
		return this;
	}

	public Scenario<P, ?>[] getScenarii() {
		return scenarii;
	}

	public StoryContext run(P pilot) {
		Context context = new Context();
		if (contextConfigurer != null) {
			contextConfigurer.accept(context);
		}

		Scenario<P, ?> lastScenario = scenarii[scenarii.length - 1];
		for (Scenario<P, ?> scenario : scenarii) {
			scenario.run(pilot, context, scenario == lastScenario);
		}
		return context.getContext(StoryContext.class);
	}

}
