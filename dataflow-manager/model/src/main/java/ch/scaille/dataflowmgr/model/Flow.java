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
		private final List<Processor> processorCalls = new ArrayList<>();
		private final IdentityHashMap<Processor, Set<Processor>> dependencies = new IdentityHashMap<>();
		private final String inputType;

		public FlowBuilder(final String flowName, final UUID uuid, final String inputType) {
			this.flowName = flowName;
			this.uuid = uuid;
			this.inputType = inputType;
		}

		public FlowBuilder add(final Processor.Builder call) {
			this.add(call.build());
			return this;
		}

		public FlowBuilder add(final Processor processorCall) {
			processorCalls.add(processorCall);
			return this;
		}

		public FlowBuilder add(final ConditionalFlowCtrl.Builder call) {
			return add(call.build());
		}

		public FlowBuilder add(final ConditionalFlowCtrl caseControl) {
			caseControl.getCalls().forEach(this::add);
			final var defaultCall = caseControl.getDefaultCall();
			if (defaultCall.getCall() != null) {
				// make default depend on all other calls
				final var defaultCallDeps = dependencies.computeIfAbsent(defaultCall, _ -> new HashSet<>());
				defaultCallDeps.addAll(caseControl.getCalls());
				defaultCallDeps.remove(defaultCall);
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
	public static final ProcessorCall EXIT_POINT = new ProcessorCall("exit", "exit", EMPTY_PARAMETERS, Void.TYPE.getName());
	public static final String ENTRY_POINT = "inputDataPoint";

	public Flow(final FlowBuilder callsBuilder) {
		super(callsBuilder.uuid);
		this.config = callsBuilder;
	}

	public String getName() {
		return config.flowName;
	}

	public List<Processor> getCalls() {
		return config.processorCalls;
	}

	public String getEntryPointType() {
		return config.inputType;
	}

	public Map<Processor, Set<Processor>> cloneDependencies() {
		return config.dependencies.entrySet().stream()
				.collect(toMap(Map.Entry::getKey, kv -> new HashSet<>(kv.getValue())));
	}

	public Set<Processor> getAllDependencies(final Processor processorCall) {
		final var deps = new HashSet<Processor>();
		deps.addAll(
				config.processorCalls.stream().filter(b -> b.toDataPoint().equals(processorCall.fromDataPoint())).collect(toSet()));
		deps.addAll(config.dependencies.getOrDefault(processorCall, emptySet()));
		return deps;
	}

}
