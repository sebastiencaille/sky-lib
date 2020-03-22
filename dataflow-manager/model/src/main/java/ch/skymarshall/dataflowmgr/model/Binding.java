package ch.skymarshall.dataflowmgr.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class Binding extends WithId {

	public static class Builder {

		private final String fromProcessor;
		private final String toProcessor;
		private final Set<BindingRule> rules = new HashSet<>();
		private final List<String> adapters = new ArrayList<>();

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

		public Builder withExternalAdapter(final String adapterName) {
			adapters.add(adapterName);
			return this;
		}

	}

	public static Builder builder(final String fromProcessor, final String toProcessor) {
		return new Builder(fromProcessor, toProcessor);
	}

	public static Builder entryBuilder(final String toProcessor) {
		return new Builder(Flow.ENTRY_PROCESSOR, toProcessor);
	}

	public static Builder exitBuilder(final String fromProcessor) {
		return new Builder(fromProcessor, Flow.EXIT_PROCESSOR);
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

	public List<String> getAdapters() {
		return config.adapters;
	}
}
