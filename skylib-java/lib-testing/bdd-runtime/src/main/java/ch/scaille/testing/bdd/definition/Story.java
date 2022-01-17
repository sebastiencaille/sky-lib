package ch.scaille.testing.bdd.definition;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

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

	private final Scenario<P, ?>[] scenarii;
	private Consumer<Context> contextConfigurer;

	public Story(Scenario<P, ?>... scenarii) {
		this.scenarii = scenarii;
	}
	
	public Story<P> withContext(Consumer<Context> contextConfigurer) {
		this.contextConfigurer = contextConfigurer;
		return this;
	}
	
	public Scenario<P, ?>[] getScenarii() {
		return scenarii;
	}

	public void run(P pilot) {
		Context context = new Context();
		if (contextConfigurer != null) {
			contextConfigurer.accept(context);
		}
		Scenario<P, ?> lastScenario = scenarii[scenarii.length - 1];
		for (Scenario<P, ?> scenario : scenarii) {
			scenario.run(pilot, context, scenario == lastScenario);
		}
	}

}
