package ch.scaille.dataflowmgr.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * How to bind data, processors and external adapters.
 * 
 * @author scaille
 *
 */
public class Processor extends WithId {

	public static class Builder {

		private final String fromDataPoint;
		private final ProcessorCall processorCall;
		private final List<CallRule> rules = new ArrayList<>();
		private final List<ExternalAdapter> adapters = new ArrayList<>();
		private String toDataPoint;

		public Builder(final String fromDataPoint, final ProcessorCall processorCall) {
			this.fromDataPoint = fromDataPoint;
			this.processorCall = processorCall;
		}

		public Processor build() {
			return new Processor(this);
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

		public Builder addRule(CallRule rule) {
			this.rules.add(rule);
			return this;
		}

		public Builder addRules(Collection<CallRule> rules) {
			this.rules.addAll(rules);
			return this;
		}

		public List<CallRule> getRules() {
			return rules;
		}

	}

	public static Builder builder(final ProcessorCall fromProcessorCall, final ProcessorCall toProcessorCall) {
		return new Builder(fromProcessorCall.asDataPoint(), toProcessorCall);
	}

	public static Builder builder(final String fromDataPoint, final ProcessorCall toProcessorCall) {
		return new Builder(fromDataPoint, toProcessorCall);
	}

	private final Builder config;

	public Processor(final Builder config) {
		super(WithId.id());
		this.config = config;
	}

	public String fromDataPoint() {
		return config.fromDataPoint;
	}

	public ProcessorCall getCall() {
		return config.processorCall;
	}

	public List<CallRule> getRules() {
		return config.rules;
	}

	public List<ExternalAdapter> getAdapters() {
		return config.adapters;
	}

	public String toDataPoint() {
		return Objects.requireNonNullElseGet(config.toDataPoint, () -> getCall().asDataPoint());
	}

	@Override
	public String toString() {
		return fromDataPoint() + " -> " + getCall().getCall() + " -> " + toDataPoint();
	}

	public boolean isExit() {
		return Flow.EXIT_PROCESSOR.equals(toDataPoint());
	}

	public boolean isEntry() {
		return Flow.ENTRY_POINT.equals(fromDataPoint());
	}

}
