package ch.scaille.testing.bdd.definition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import ch.scaille.testing.bdd.definition.ScenarioFragment.Step;

public class Scenario<PP> {

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

	public static class ExecutionContext {
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

		public void addWhen(Step<?> step) {
			add(" When ", step);
		}

		public void addThen(Step<?> step) {
			add(" Then ", step);
		}

		@Override
		public String toString() {
			return String.join("\n", report);
		}

	}

	private final ScenarioFragment<PP>[] scenarii;
	private Consumer<Context> contextConfigurer;

	public Scenario(ScenarioFragment<PP>... scenarii) {
		this.scenarii = scenarii;
	}

	public Consumer<Context> getContextConfigurer() {
		return contextConfigurer;
	}

	public Scenario<PP> withContext(Consumer<Context> contextConfigurer) {
		this.contextConfigurer = contextConfigurer;
		return this;
	}

	public ScenarioFragment<PP>[] getScenarii() {
		return scenarii;
	}

	public Scenario<PP> followedBy(ScenarioFragment<PP> next) {
		ScenarioFragment<PP>[] newScenarii = Arrays.copyOf(getScenarii(), getScenarii().length + 1);
		newScenarii[newScenarii.length - 1] = next;
		return new Scenario<>(newScenarii);
	}

	public ExecutionContext run(PP pageProvider) {
		Context context = new Context();
		if (contextConfigurer != null) {
			contextConfigurer.accept(context);
		}

		ScenarioFragment<PP> lastScenario = scenarii[scenarii.length - 1];
		for (ScenarioFragment<PP> scenario : scenarii) {
			scenario.run(pageProvider, context, scenario == lastScenario);
		}
		return context.getContext(ExecutionContext.class);
	}


}
