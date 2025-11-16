package ch.scaille.dataflowmgr.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * How to bind data, processors and external adapters.
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

		public Binding build(final Binding... someParents) {
			this.parents.addAll(List.of(someParents));
			return new Binding(this);
		}

		/**
		 * Allows accessing an external adapter. The adapter will be called before the
		 * conditions if needed.
		 */
		public Builder withExternalData(final ExternalAdapter adapter) {
			adapters.add(adapter);
			return this;
		}

		public Builder as(final String dataPoint) {
			this.toDataPoint = dataPoint;
			return this;
		}

		public Builder addRule(BindingRule rule) {
			this.rules.add(rule);
			return this;
		}

		public Builder addRules(Collection<BindingRule> rules) {
			this.rules.addAll(rules);
			return this;
		}

		public List<BindingRule> getRules() {
			return rules;
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
		super(WithId.id());
		this.config = config;
	}

	public String fromDataPoint() {
		return config.fromDataPoint;
	}

	public Processor getProcessor() {
		return config.processor;
	}

	public List<BindingRule> getRules() {
		return config.rules;
	}

	public List<ExternalAdapter> getAdapters() {
		return config.adapters;
	}

	public String toDataPoint() {
		return Objects.requireNonNullElseGet(config.toDataPoint, () -> getProcessor().asDataPoint());
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
