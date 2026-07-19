package ch.scaille.dataflowmgr.model.flowctrl;

import static ch.scaille.util.helpers.StreamExt.notEq;
import static java.util.stream.Collectors.toSet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import ch.scaille.dataflowmgr.model.Call;
import ch.scaille.dataflowmgr.model.Processor;
import ch.scaille.dataflowmgr.model.CallRule;
import ch.scaille.dataflowmgr.model.CallRule.Type;
import ch.scaille.dataflowmgr.model.GenericCall;
import ch.scaille.dataflowmgr.model.IFlowCheck;
import ch.scaille.dataflowmgr.model.WithId;

public class ConditionalFlowCtrl extends WithId implements IFlowCheck {

	public static class Builder {

		private final List<Processor.Builder> calls = new ArrayList<>();

		private String check;

		private final String name;

		private boolean exclusive;

		public Builder(final String name) {
			this.name = name;
		}

		public void setExclusive(final boolean exclusive) {
			this.exclusive = exclusive;
		}

		public Builder setCheck(final String check) {
			this.check = check;
			return this;
		}

		public Builder conditional(GenericCall condition, final Processor.Builder call) {
			calls.add(call.addRule(activator(condition)));
			return this;
		}

		public Builder fallback(final Processor.Builder call) {
			if (getActivators(call.getRules()).findAny().isPresent() && findDefaultCall().isPresent()) {
				throw new IllegalStateException("A default call is already present");
			}
			calls.add(call);
			return this;
		}

		public Processor.Builder getDefaultCall() {
			return findDefaultCall()
					.orElseGet(() -> {
						final var firstCall = calls.getFirst().build();
						return new Processor.Builder(firstCall.fromDataPoint(), null).as(firstCall.toDataPoint());
					});
		}

		private Optional<Processor.Builder> findDefaultCall() {
			return calls.stream().filter(b -> getActivators(b.getRules()).findAny().isEmpty()).findAny();
		}

		public ConditionalFlowCtrl build() {
			// TODO: check that all "from"" are the same
			return new ConditionalFlowCtrl(this);
		}
	}

	public static Builder builder(final String name) {
		return new Builder(name);
	}

	private final Builder config;

	private final List<Processor> processorCalls = new ArrayList<>();
	private final Processor defaultProcessorCall;

	public ConditionalFlowCtrl(final Builder configuration) {
		super(UUID.randomUUID());
		this.config = configuration;

		final var defaultCallBuilder = config.getDefaultCall();
		processorCalls.addAll(config.calls.stream().filter(notEq(defaultCallBuilder))
				.map(b -> b.addRule(condition(this)).build()).toList());

		this.defaultProcessorCall = defaultCallBuilder.addRule(condition(this))
						.addRules(processorCalls.stream()
								.map(ConditionalFlowCtrl::exclusion)
								.collect(toSet()))
						.build();
		processorCalls.add(this.defaultProcessorCall);

	}

	public List<Processor> getCalls() {
		return processorCalls;
	}

	public Processor getDefaultCall() {
		return defaultProcessorCall;
	}

	@Override
	public String getCheck() {
		return config.check;
	}

	public String getName() {
		return config.name;
	}

	public boolean isExclusive() {
		return config.exclusive;
	}

	public static CallRule activator(final GenericCall condition) {
		return new CallRule(Type.ACTIVATION, condition);
	}

	public static Stream<Call> getActivators(final Collection<CallRule> rules) {
		return CallRule.getAll(rules, Type.ACTIVATION, Call.class);
	}

	public static CallRule exclusion(final Processor exclusion) {
		return new CallRule(Type.EXCLUSION, exclusion);
	}

	public static <T> Stream<T> getExclusions(Collection<CallRule> rules, Class<T> clazz) {
		return CallRule.getAll(rules, CallRule.Type.EXCLUSION, clazz);
	}

	public static CallRule condition(final ConditionalFlowCtrl condition) {
		return new CallRule(Type.CONDITIONAL, condition);
	}

	public static Optional<ConditionalFlowCtrl> getCondition(final Collection<CallRule> rules) {
		return CallRule.getAll(rules, Type.CONDITIONAL, ConditionalFlowCtrl.class).findFirst();
	}

}
