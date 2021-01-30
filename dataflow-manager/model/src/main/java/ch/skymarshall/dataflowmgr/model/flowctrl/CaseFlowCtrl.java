package ch.skymarshall.dataflowmgr.model.flowctrl;

import static ch.skymarshall.util.helpers.StreamHelper.notEq;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import ch.skymarshall.dataflowmgr.model.Binding;
import ch.skymarshall.dataflowmgr.model.BindingRule;
import ch.skymarshall.dataflowmgr.model.BindingRule.Type;
import ch.skymarshall.dataflowmgr.model.CustomCall;
import ch.skymarshall.dataflowmgr.model.IFlowCheck;
import ch.skymarshall.dataflowmgr.model.WithId;

public class CaseFlowCtrl extends WithId implements IFlowCheck {

	public static class Builder {

		private final List<Binding.Builder> bindings = new ArrayList<>();

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

		public Builder add(CustomCall condition, final Binding.Builder binding) {
			bindings.add(binding.addRule(activator(condition)));
			return this;
		}

		public Builder defaultCase(final Binding.Builder binding) {
			if (getActivators(binding.getRules()).count() > 0 && getDefaultBinding().isPresent()) {
				throw new IllegalStateException("A default binding is already present");
			}
			bindings.add(binding);
			return this;
		}

		public Optional<Binding.Builder> getDefaultBinding() {
			return bindings.stream().filter(b -> getActivators(b.getRules()).count() == 0).findAny();
		}

		public CaseFlowCtrl build() {
			return new CaseFlowCtrl(this);
		}
	}

	public static Builder builder(final String name) {
		return new Builder(name);
	}

	private final Builder config;

	private final List<Binding> bindings = new ArrayList<>();
	private final Optional<Binding> defaultBinding;

	public CaseFlowCtrl(final Builder configuration) {
		super(UUID.randomUUID());
		this.config = configuration;

		final Optional<Binding.Builder> defaultBindingBuilder = config.getDefaultBinding();
		bindings.addAll(config.bindings.stream().filter(notEq(defaultBindingBuilder.orElse(null)))
				.map(b -> b.addRule(condition(this)).build()).collect(toList()));

		this.defaultBinding = defaultBindingBuilder
				.map(b -> b.addRule(condition(this)).addRules(bindings.stream().map(CaseFlowCtrl::exclusion).collect(toSet())).build());
		this.defaultBinding.ifPresent(bindings::add);
	}

	public List<Binding> getBindings() {
		return bindings;
	}

	public Optional<Binding> getDefaultBinding() {
		return defaultBinding;
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

	public static BindingRule activator(final CustomCall condition) {
		return new BindingRule(Type.ACTIVATION, condition);
	}

	public static CustomCall activator(final BindingRule rule) {
		return rule.get(CustomCall.class);
	}

	public static Stream<CustomCall> getActivators(final Collection<BindingRule> rules) {
		return BindingRule.getAll(rules, Type.ACTIVATION, CustomCall.class);
	}

	public static BindingRule exclusion(final Binding exclusion) {
		return new BindingRule(Type.EXCLUSION, exclusion);
	}

	public static Stream<Binding> getExclusions(Collection<BindingRule> rules) {
		return BindingRule.getAll(rules, BindingRule.Type.EXCLUSION, Binding.class);
	}

	public static BindingRule condition(final CaseFlowCtrl condition) {
		return new BindingRule(Type.CONDITIONAL, condition);
	}

	public static Optional<CaseFlowCtrl> getCondition(final Collection<BindingRule> rules) {
		return BindingRule.getAll(rules, Type.CONDITIONAL, CaseFlowCtrl.class).findFirst();
	}

}
