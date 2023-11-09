package ch.scaille.dataflowmgr.model;

import static java.util.Collections.emptySet;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import ch.scaille.dataflowmgr.model.flowctrl.ConditionalFlowCtrl;

public class Flow extends WithId {

	private static final LinkedHashMap<String, String> EMPTY_PARAMETERS = new LinkedHashMap<>();

	public static class FlowBuilder {
		final String flowName;
		final UUID uuid;
		private final List<Binding> bindings = new ArrayList<>();
		private final IdentityHashMap<Binding, Set<Binding>> dependencies = new IdentityHashMap<>();
		private final String inputType;

		public FlowBuilder(final String flowName, final UUID uuid, final String inputType) {
			this.flowName = flowName;
			this.uuid = uuid;
			this.inputType = inputType;
		}

		public FlowBuilder add(final Binding.Builder binding) {
			this.add(binding.build());
			return this;
		}

		public FlowBuilder add(final Binding binding) {
			bindings.add(binding);
			return this;
		}

		public FlowBuilder add(final ConditionalFlowCtrl.Builder binding) {
			return add(binding.build());
		}

		public FlowBuilder add(final ConditionalFlowCtrl caseControl) {
			caseControl.getBindings().forEach(this::add);
			final var defaultBinding = caseControl.getDefaultBinding();
			if (defaultBinding.isPresent()) {
				// make default depend on all other bindings
				final var defaultBindingDeps = dependencies.computeIfAbsent(defaultBinding.get(), b -> new HashSet<>());
				defaultBindingDeps.addAll(caseControl.getBindings());
				defaultBindingDeps.remove(defaultBinding.get());
			}
			return this;
		}

		public Flow build() {
			return new Flow(this);
		}
	}

	public static FlowBuilder builder(final String flowName, final UUID uuid, final String inputType) {
		return new FlowBuilder(flowName, uuid, inputType);
	}

	private final FlowBuilder config;

	public static final String EXIT_PROCESSOR = "exit";
	public static final Processor EXIT_POINT = new Processor("exit", "exit", EMPTY_PARAMETERS, Void.TYPE.getName());
	public static final String ENTRY_POINT = "inputDataPoint";

	public Flow(final FlowBuilder bindingsBuilder) {
		super(bindingsBuilder.uuid);
		this.config = bindingsBuilder;
	}

	public String getName() {
		return config.flowName;
	}

	public List<Binding> getBindings() {
		return config.bindings;
	}

	public String getEntryPointType() {
		return config.inputType;
	}

	public Map<Binding, Set<Binding>> cloneDependencies() {
		return config.dependencies.entrySet().stream()
				.collect(toMap(Map.Entry::getKey, kv -> new HashSet<>(kv.getValue())));
	}

	public Set<Binding> getAllDependencies(final Binding binding) {
		final var deps = new HashSet<Binding>();
		deps.addAll(
				config.bindings.stream().filter(b -> b.toDataPoint().equals(binding.fromDataPoint())).collect(toSet()));
		deps.addAll(config.dependencies.getOrDefault(binding, emptySet()));
		return deps;
	}

}
