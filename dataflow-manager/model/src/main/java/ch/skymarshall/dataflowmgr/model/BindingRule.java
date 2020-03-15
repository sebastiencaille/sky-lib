package ch.skymarshall.dataflowmgr.model;

import java.util.Optional;
import java.util.Set;
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

	public static BindingRule activator(final String activator) {
		return new BindingRule(Type.ACTIVATION, activator);
	}

	public static Optional<String> getActivator(final Set<BindingRule> rules) {
		return BindingRule.get(rules, BindingRule.Type.ACTIVATION).map(BindingRule::string);
	}

	public static BindingRule exclusion(final Binding exclusion) {
		return new BindingRule(Type.EXCLUSION, exclusion);
	}

	public static BindingRule condition(final ConditionalBindingGroup condition) {
		return new BindingRule(Type.CONDITIONAL, condition);
	}

	public static Optional<BindingRule> get(final Set<BindingRule> rules, final Type type) {
		return rules.stream().filter(r -> r.ruleType == type).findAny();
	}

	public static Stream<BindingRule> getAll(final Set<BindingRule> rules, final Type type) {
		return rules.stream().filter(r -> r.ruleType == type);
	}

}
