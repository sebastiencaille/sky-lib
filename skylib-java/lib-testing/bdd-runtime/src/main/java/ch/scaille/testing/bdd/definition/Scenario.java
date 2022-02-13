package ch.scaille.testing.bdd.definition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import ch.scaille.testing.bdd.definition.Steps.Step;

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

	private final Steps<PP>[] steps;
	private Consumer<Context> contextConfigurer;

	public Scenario(Steps<PP>... steps) {
		this.steps = steps;
	}

	public Consumer<Context> getContextConfigurer() {
		return contextConfigurer;
	}

	public Scenario<PP> withContext(Consumer<Context> contextConfigurer) {
		this.contextConfigurer = contextConfigurer;
		return this;
	}

	public Steps<PP>[] getScenarii() {
		return steps;
	}

	public Scenario<PP> followedBy(Steps<PP>... nexts) {
		Steps<PP>[] newsteps = Arrays.copyOf(steps, steps.length + nexts.length);
		System.arraycopy(nexts, 0, newsteps, steps.length, nexts.length);
		return new Scenario<>(newsteps);
	}

	public ExecutionContext run(PP pageProvider) {
		Context context = new Context();
		if (contextConfigurer != null) {
			contextConfigurer.accept(context);
		}

		Steps<PP> lastStep = steps[steps.length - 1];
		for (Steps<PP> step : steps) {
			step.run(pageProvider, context, step == lastStep);
		}
		return context.getContext(ExecutionContext.class);
	}

}
