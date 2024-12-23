package ch.scaille.tcwriter.pilot.selenium;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.bidi.module.LogInspector;
import org.openqa.selenium.bidi.module.Script;
import org.openqa.selenium.bidi.script.EvaluateResult;
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

	public interface IBiDiEvent {
		BiDiEventConfig config();
	}

	public static class BiDiEventConfig {
		private final String mutationType;

		private final String eventLambda;

		private String observerConfig;

		private String componentXpath;

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


	public class BidiEventWaiter extends EventWaiter<T> {

		public BidiEventWaiter() {
			try (Script script = new Script(webDriver)) {
				// Create the observers
				for (T event : events) {
					final EvaluateResult result = script.evaluateFunctionInRealm(script.getAllRealms().get(0).getRealmId(), toJs(event), true, Optional.empty());
					if (result.getResultType() == Type.EXCEPTION) {
						throw new IllegalStateException("Script installation failed");
					}
				}
				// Handle the log produced by the observer
				try (LogInspector logInspector = new LogInspector(webDriver)) {
					logInspector.onConsoleEntry(entry -> {
						if (entry.getText().startsWith(EVENT_MARKER) && entry.getText().contains(uuid) ) {
							final String enumName = entry.getText().substring(EVENT_MARKER.length() + uuid.length() + 2);
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
		
	}
	
	private final WebDriver webDriver;
	private final T[] events;

	private String uuid = UUID.randomUUID().toString();

	public BiDiEvent(WebDriver webDriver, T... events) {
		this.webDriver = webDriver;
		this.events = events;
	}

	@Override
	public EventWaiter<T> expect() {
		return new BidiEventWaiter();
	}

}