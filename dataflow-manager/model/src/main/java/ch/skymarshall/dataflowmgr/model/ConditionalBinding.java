package ch.skymarshall.dataflowmgr.model;

import static ch.skymarshall.dataflowmgr.model.BindingRule.getActivator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ConditionalBinding implements IFlowCheck {

	public static class Builder {

		private final List<Binding> bindings = new ArrayList<>();

		private String check;

		private final String name;

		public Builder(final String name) {
			this.name = name;
		}

		public Builder setCheck(final String check) {
			this.check = check;
			return this;
		}

		public Builder add(final Binding.Builder binding) {
			return add(binding.build());
		}

		public Builder add(final Binding binding) {
			if (getActivator(binding).isPresent() && getDefaultBinding().isPresent()) {
				throw new IllegalStateException("Duplicate default binding");
			}
			bindings.add(binding);
			return this;
		}

		public Optional<Binding> getDefaultBinding() {
			return bindings.stream().filter(b -> !getActivator(b).isPresent()).findAny();
		}

		public ConditionalBinding build() {
			final Optional<Binding> defaultBinding = getDefaultBinding();
			if (defaultBinding.isPresent()) {
				defaultBinding.get().addExclusions(
						bindings.stream().filter(b -> !b.equals(defaultBinding.get())).collect(Collectors.toSet()));
			}
			return new ConditionalBinding(this);
		}
	}

	public static Builder builder(final String name) {
		return new Builder(name);
	}

	private final Builder config;

	public ConditionalBinding(final Builder configuration) {
		this.config = configuration;
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

}
