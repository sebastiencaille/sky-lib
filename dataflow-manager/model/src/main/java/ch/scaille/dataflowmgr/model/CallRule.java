package ch.scaille.dataflowmgr.model;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

public class CallRule extends WithId {

	public enum Type {
		// Activate call according to conditions
		ACTIVATION,
		// Skip call according to conditions
		EXCLUSION,
		// Conditional control, used to trigger activations and exclusions
		CONDITIONAL
	}

	private final Type ruleType;
	private final Object payload;

	public CallRule(final Type rule, final Object payload) {
		super(UUID.randomUUID());
		this.ruleType = rule;
		this.payload = payload;
	}

	public Type getRule() {
		return ruleType;
	}

	public <T> Optional<T> get(final Class<T> clazz) {
		if (clazz.isInstance(payload)) {
			return Optional.of(clazz.cast(payload));
		}
		return Optional.empty();
	}

	@Override
	public String toString() {
		return ruleType + ":" + payload;
	}

	public static Stream<CallRule> getAll(final Collection<CallRule> rules, final Type type) {
		return rules.stream().filter(r -> r.ruleType == type);
	}

	public static <T> Stream<T> getAll(final Collection<CallRule> rules, final Type type, final Class<T> cast) {
		return rules.stream().filter(r -> r.ruleType == type).flatMap(r -> r.get(cast).stream());
	}

}
