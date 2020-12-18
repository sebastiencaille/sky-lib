package ch.skymarshall.dataflowmgr.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * How to bind data, processors and external adapters.
 * 
 * TODO: add aliases to override call parameters and call results 
 * 
 * @author scaille
 *
 */
public class Binding extends WithId {

	public static class Builder {

		private final String fromDataPoint;
		private final Processor processor;
		private final List<BindingRule> rules = new ArrayList<>();
		private final List<ExternalAdapter> adapters = new ArrayList<>();
		private final Set<Binding> parents = new HashSet<>();
		private String toDataPoint;

		public Builder(final String fromDataPoint, final Processor processor) {
			this.fromDataPoint = fromDataPoint;
			this.processor = processor;
		}

		public Builder activateIf(final Condition condition) {
			rules.add(BindingRule.activator(condition));
			return this;
		}

		public Binding build(final Binding... someParents) {
			this.parents.addAll(Arrays.asList(someParents));
			return new Binding(this);
		}

		/**
		 * Adds an access to an external adapter. The adapter will be called before the
		 * conditions if needed.
		 * 
		 * @param adapter
		 * @return
		 */
		public Builder withExternalData(final ExternalAdapter adapter) {
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

	public static Builder builder(Condition condition, final Processor fromProcessor, final Processor toProcessor) {
		return new Builder(fromProcessor.asDataPoint(), toProcessor).activateIf(condition);
	}

	public static Builder builder(final String fromDataPoint, final Processor toProcessor) {
		return new Builder(fromDataPoint, toProcessor);
	}

	public static Builder builder(Condition condition, final String fromDataPoint, final Processor toProcessor) {
		return new Builder(fromDataPoint, toProcessor).activateIf(condition);
	}

	private final Builder config;

	public Binding(final Builder config) {
		super(WithId.id());
		this.config = config;
	}

	public String fromDataPoint() {
		return config.fromDataPoint;
	}

	public Processor getProcessor() {
		return config.processor;
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

	public String toDataPoint() {
		if (config.toDataPoint != null) {
			return config.toDataPoint;
		}
		return getProcessor().asDataPoint();
	}

	@Override
	public String toString() {
		return fromDataPoint() + " -> " + getProcessor().getCall() + " -> " + toDataPoint();
	}

	public boolean isExit() {
		return Flow.EXIT_PROCESSOR.equals(toDataPoint());
	}

	public boolean isEntry() {
		return Flow.ENTRY_POINT.equals(fromDataPoint());
	}

}
