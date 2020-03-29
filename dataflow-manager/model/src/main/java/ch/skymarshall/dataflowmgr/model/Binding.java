package ch.skymarshall.dataflowmgr.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class Binding extends WithId {

	public static class Builder {

		private final String fromDataPoint;
		private final Processor toProcessor;
		private final List<BindingRule> rules = new ArrayList<>();
		private final List<ExternalAdapter> adapters = new ArrayList<>();
		private final Set<Binding> parents = new HashSet<>();
		private String toDataPoint;

		public Builder(final String fromDataPoint, final Processor toProcessor) {
			this.fromDataPoint = fromDataPoint;
			this.toProcessor = toProcessor;
		}

		public Builder activator(final Condition isEnhance) {
			rules.add(BindingRule.activator(isEnhance));
			return this;
		}

		public Binding build(final Binding... someParents) {
			this.parents.addAll(Arrays.asList(someParents));
			return new Binding(this);
		}

		public Builder withExternalAdapter(final ExternalAdapter adapter) {
			adapters.add(adapter);
			return this;
		}

		public Builder as(final String dataPoint) {
			this.toDataPoint = dataPoint;
			return this;
		}

	}

	public static Builder builder(final Processor fromProcessor, final Processor toProcessor) {
		return new Builder(fromProcessor.asDataPoint(), toProcessor);
	}

	public static Builder builder(final String fromDataPoint, final Processor toProcessor) {
		return new Builder(fromDataPoint, toProcessor);
	}

	private final Builder config;

	public Binding(final Builder config) {
		super(UUID.randomUUID());
		this.config = config;
	}

	public String fromDataPoint() {
		return config.fromDataPoint;
	}

	public Processor toProcessor() {
		return config.toProcessor;
	}

	public void addRule(final BindingRule rule) {
		config.rules.add(rule);
	}

	public void addRule(final Set<BindingRule> rules) {
		config.rules.addAll(rules);
	}

	public List<BindingRule> getRules() {
		return config.rules;
	}

	public List<ExternalAdapter> getAdapters() {
		return config.adapters;
	}

	public String outputName() {
		if (config.toDataPoint != null) {
			return config.toDataPoint;
		}
		return toProcessor().asDataPoint();
	}

}
