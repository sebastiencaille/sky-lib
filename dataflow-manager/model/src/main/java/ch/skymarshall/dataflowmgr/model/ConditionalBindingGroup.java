package ch.skymarshall.dataflowmgr.model;

import static ch.skymarshall.dataflowmgr.model.BindingRule.getActivator;
import static ch.skymarshall.util.helpers.StreamHelper.notEq;
import static java.util.stream.Collectors.toSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ConditionalBindingGroup extends WithId implements IFlowCheck {

	public static class Builder {

		private final List<Binding> bindings = new ArrayList<>();

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

		public Builder add(final Binding.Builder binding) {
			return add(binding.build());
		}

		public Builder add(final Binding binding) {
			if (getActivator(binding.getRules()).isPresent() && getDefaultBinding().isPresent()) {
				throw new IllegalStateException("Duplicate default binding");
			}
			bindings.add(binding);
			return this;
		}

		public Optional<Binding> getDefaultBinding() {
			return bindings.stream().filter(b -> !getActivator(b.getRules()).isPresent()).findAny();
		}

		public ConditionalBindingGroup build() {
			final Optional<Binding> defaultBinding = getDefaultBinding();
			if (defaultBinding.isPresent()) {
				defaultBinding.get().addRule(bindings.stream().filter(notEq(defaultBinding.get()))
						.map(BindingRule::exclusion).collect(toSet()));
			}
			return new ConditionalBindingGroup(this);
		}
	}

	public static Builder builder(final String name) {
		return new Builder(name);
	}

	private final Builder config;

	public ConditionalBindingGroup(final Builder configuration) {
		super(UUID.randomUUID());
		this.config = configuration;
		this.config.bindings.forEach(b -> b.addRule(BindingRule.condition(this)));
	}

	public List<Binding> getBindings() {
		return config.bindings;
	}

	public Optional<Binding> getDefaultBinding() {
		return config.getDefaultBinding();
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

}
