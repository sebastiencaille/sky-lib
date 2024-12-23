package ch.scaille.tcwriter.pilot.selenium;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.bidi.module.LogInspector;
import org.openqa.selenium.bidi.module.Script;
import org.openqa.selenium.bidi.script.EvaluateResult.Type;

import ch.scaille.tcwriter.pilot.AbstractEvent;

public class BiDiEvent<T extends Enum<T> & ch.scaille.tcwriter.pilot.selenium.BiDiEvent.IBiDiEvent>
		extends AbstractEvent<T> {
	
	public enum MutationConfig {
		CHILD_LIST("childList"),
		ATTRIBUTES("attributes"),
		ATTRIBUTE_OLD_VALUE("attributeOldValue"),
		CHARACTER_DATA("characterData"),
		CHARACTER_DATA_OLD_VALUE("characterDataOldValue");

		private String js;

		MutationConfig(String js) {
			this.js = js;
		}
		
		public String js() {
			return String.format("%s: true", js);
		}
	}
	
	private static final String EVENT_MARKER = BiDiEvent.class.getSimpleName();
	
	private static final String PATH_TEMPLATE =
			"if (typeof EventWaiter_%s === 'undefined') {"
			+ "EventWaiter_%s = true;"
			+ "const targetNode = document.evaluate(\"%s\", document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue;"
			+ "const callback = function (mutationsList, observer) {" 
			+ "    for (let mutation of mutationsList) {" 
			+ "        console.log('Mutation:' + mutation.type);"
			+ "        if (mutation.type === '%s' && (%s).apply(this, [mutation])) {" 
			+ "           console.log('%s %s %s');"
			+ "        }" 
			+ "     }"
			+"    };"
			+ "const observer = new MutationObserver(callback);"
			+ "observer.observe(targetNode, %s);" 
			+ "}"; 

	
	/**
	 * Interface implemented by Event's Enum
	 */
	public interface IBiDiEvent {
		BiDiEventConfig config();
	}

	/**
	 * Basic config for mutation
	 */
	public static class BiDiEventConfig {
		private final String mutationType;

		private final String eventLambda;

		private final String observerConfig;

		private final String componentXpath;

		public BiDiEventConfig(String componentXpath, MutationConfig config, boolean subtree, String eventLambda) {
			this.componentXpath = componentXpath;
			this.mutationType = config.js;
			this.observerConfig = String.format("{ %s, subtree: %s }", config.js(), subtree);
			this.eventLambda = eventLambda;
		}
		
		public String getMutationType() {
			return mutationType;
		}

		public String getEventLambda() {
			return eventLambda;
		}
		
		public String getObserverConfig() {
			return observerConfig;
		}
		
		public String getComponentXpath() {
			return componentXpath;
		}
	}

	/**
	 * Allows to wait for the event
	 */
	public class BidiEventWaiter extends EventWaiter<T> {

		public BidiEventWaiter(Predicate<List<T>> historyTest) {
			super(historyTest);
			try (var script = new Script(pilot.getDriver())) {
				// Create the observers
				for (var event : events) {
					final var result = script.evaluateFunctionInRealm(script.getAllRealms().get(0).getRealmId(), toJs(event), true, Optional.empty());
					if (result.getResultType() == Type.EXCEPTION) {
						throw new IllegalStateException("Script installation failed");
					}
				}
				// Handle the log produced by the observer
				try (var logInspector = new LogInspector(pilot.getDriver())) {
					logInspector.onConsoleEntry(entry -> {
						if (entry.getText().startsWith(EVENT_MARKER) && entry.getText().contains(uuid) ) {
							final var enumName = entry.getText().substring(EVENT_MARKER.length() + uuid.length() + 2);
							Arrays.stream(events)
									.filter(e -> e.name().equals(enumName))
									.findAny()
									.ifPresent(this::onEvent);
						}
					});
				}
			}
		}
		
		protected String toJs(T event) {
			return String.format(PATH_TEMPLATE, event.name(), event.name(), event.config().getComponentXpath(), event.config().getMutationType(), event.config().getEventLambda(), 
					EVENT_MARKER, uuid, event.name(), event.config().getObserverConfig());
		}
		
		public boolean matches() throws InterruptedException {
			return super.matches(pilot.getPollingTimeout().toMillis(), TimeUnit.MILLISECONDS);
		}
		
		@Override
		public void assertMatches() throws InterruptedException {
			Assertions.assertTrue(matches(), "Found events: " + received());
		}
	}
	
	private final SeleniumPilot pilot;
	private final T[] events;

	private final String uuid = UUID.randomUUID().toString();

	public BiDiEvent(SeleniumPilot pilot, T... events) {
		this.pilot = pilot;
		this.events = events;
	}

	@Override
	public EventWaiter<T> expect(Predicate<List<T>> historyTest) {
		return new BidiEventWaiter(historyTest);
	}

}