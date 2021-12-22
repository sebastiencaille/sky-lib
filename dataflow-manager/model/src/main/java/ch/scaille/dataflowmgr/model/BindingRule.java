package ch.scaille.dataflowmgr.model;

import java.util.Collection;
import java.util.UUID;
import java.util.stream.Stream;

public class BindingRule extends WithId {

	public enum Type {
		EXCLUSION, ACTIVATION, CONDITIONAL
	}

	private final Type ruleType;
	private final Object payload;

	public BindingRule(final Type rule, final Object payload) {
		super(UUID.randomUUID());
		this.ruleType = rule;
		this.payload = payload;
	}

	public Type getRule() {
		return ruleType;
	}

	public <T> T get(final Class<T> clazz) {
		return clazz.cast(payload);
	}

	@Override
	public String toString() {
		return ruleType + ":" + payload;
	}

	public String string() {
		return get(String.class);
	}

	public static Stream<BindingRule> getAll(final Collection<BindingRule> rules, final Type type) {
		return rules.stream().filter(r -> r.ruleType == type);
	}

	public static <T> Stream<T> getAll(final Collection<BindingRule> rules, final Type type, final Class<T> cast) {
		return rules.stream().filter(r -> r.ruleType == type).map(r -> r.get(cast));
	}

}
