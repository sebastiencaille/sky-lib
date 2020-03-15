package ch.skymarshall.dataflowmgr.model;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Binding extends WithId {

	public static class Builder {

		private final String fromProcessor;
		private final String toProcessor;
		private final Set<BindingRule> rules = new HashSet<>();

		public Builder(final String fromProcessor, final String toProcessor) {
			this.fromProcessor = fromProcessor;
			this.toProcessor = toProcessor;
		}

		public Builder activator(final String activator) {
			rules.add(BindingRule.activator(activator));
			return this;
		}

		public Binding build() {
			return new Binding(this);
		}

	}

	public static Builder builder(final String entryProcessor, final String string) {
		return new Builder(entryProcessor, string);
	}

	private final Builder config;

	public Binding(final Builder config) {
		super(UUID.randomUUID());
		this.config = config;
	}

	public String fromProcessor() {
		return config.fromProcessor;
	}

	public String toProcessor() {
		return config.toProcessor;
	}

	public void addRule(final BindingRule rule) {
		config.rules.add(rule);
	}

	public void addRule(final Set<BindingRule> rules) {
		config.rules.addAll(rules);
	}

	public Set<BindingRule> getRules() {
		return config.rules;
	}

}
