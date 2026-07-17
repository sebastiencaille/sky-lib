package ch.scaille.dataflowmgr.generator.writers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ch.scaille.dataflowmgr.model.Processor;
import ch.scaille.dataflowmgr.model.Call;
import ch.scaille.dataflowmgr.model.ExternalAdapter;
import ch.scaille.dataflowmgr.model.Flow;
import ch.scaille.dataflowmgr.model.ProcessorCall;

public abstract class AbstractFlowVisitor {

	public static class CallContext {

		public final Processor processor;
		public final List<ExternalAdapter> callAdapters;
		public final Set<ExternalAdapter> processedAdapters;

		public final String inputDataPoint;
		public final String inputDataType;
		public final String outputDataPoint;
		private List<Processor> reverseDeps;

		public CallContext(final Processor processor, final String inputDataType) {
			this.processor = processor;
			this.callAdapters = processor.getAdapters();
			this.processedAdapters = new HashSet<>();
			this.inputDataPoint = processor.fromDataPoint();
			this.inputDataType = inputDataType;
			this.outputDataPoint = processor.toDataPoint();
		}

		public Set<ExternalAdapter> unprocessedAdapters(final Collection<ExternalAdapter> adapters) {
			final var unprocessed = new HashSet<>(adapters);
			adapters.removeAll(processedAdapters);
			return unprocessed;
		}

		public List<Processor> getReverseDeps() {
			return reverseDeps;
		}

		public void setReverseDeps(final List<Processor> reverseDeps) {
			this.reverseDeps = reverseDeps;
		}

		public boolean isExit() {
			return Flow.EXIT_PROCESSOR.equals(outputDataPoint);
		}

		public ProcessorCall getProcessorCall() {
			return processor.getCall();
		}

	}

	protected final Flow flow;
	private final Map<Processor, Set<Processor>> missingDeps;
	private final Set<String> conditionalState = new HashSet<>();
	private final Set<Processor> untriggeredProcessorCalls;
	private final Map<String, String> availableDataPoints = new HashMap<>();
	private final Map<Processor, List<Processor>> reverseDeps = new HashMap<>();
	protected final List<CallContext> processOrder = new ArrayList<>();

	protected abstract void process(CallContext context);

	protected AbstractFlowVisitor(final Flow flow) {
		this.flow = flow;
		this.missingDeps = flow.cloneDependencies();
		this.untriggeredProcessorCalls = new HashSet<>(flow.getCalls());
	}

	public boolean isConditionalData(final String input) {
		return conditionalState.contains(input);
	}

	protected void processFlow() {
		availableDataPoints.put(Flow.ENTRY_POINT, flow.getEntryPointType());
		while (!untriggeredProcessorCalls.isEmpty()) {
			// More to process
			final var newlyTriggeredCalls = getTriggeredCalls();
			untriggeredProcessorCalls.removeAll(newlyTriggeredCalls);
			// make data point available only if all corresponding calls have been
			// executed
			for (final var call : newlyTriggeredCalls) {
				if (untriggeredProcessorCalls.stream().noneMatch(b -> call.toDataPoint().equals(b.toDataPoint()))) {
					availableDataPoints.put(call.toDataPoint(), call.getCall().getReturnType());
				}
			}
			missingDeps.values().forEach(v -> v.removeAll(newlyTriggeredCalls));
		}

		for (final var c : processOrder) {
			c.setReverseDeps(reverseDeps.getOrDefault(c.processor, Collections.emptyList()));
			process(c);
		}
	}

	private Set<Processor> getTriggeredCalls() {
		final var newlyTriggeredCalls = new HashSet<Processor>();
		for (final var call : untriggeredProcessorCalls) {
			// Next potential call
			final var callDeps = missingDeps.get(call);
			final boolean depsTriggered = callDeps == null || callDeps.isEmpty();
			if (!availableDataPoints.containsKey(call.fromDataPoint()) || !depsTriggered) {
				continue;
			}

			// Process
			final var context = new CallContext(call, availableDataPoints.get(call.fromDataPoint()));
			processOrder.add(context);
			for (final var dep : flow.getAllDependencies(call)) {
				reverseDeps.computeIfAbsent(dep, v -> new ArrayList<>()).add(context.processor);
			}

			newlyTriggeredCalls.add(call);
		}
		if (!untriggeredProcessorCalls.isEmpty() && newlyTriggeredCalls.isEmpty()) {
			throw new IllegalStateException("Remaining calls cannot be processed: " + untriggeredProcessorCalls);
		}
		return newlyTriggeredCalls;
	}

	/**
	 * Lists the adapters required by the activators
	 */
	public Set<ExternalAdapter> listAdapters(final CallContext context, final Call call) {
		final var adaptersRequiredByActivator = new HashSet<ExternalAdapter>();
		for (final var param : call.getParameters().entrySet()) {
			for (final var adapter : context.callAdapters) {
				final boolean match = adapter.getName().endsWith('.' + param.getKey())
						|| adapter.getReturnType().equals(param.getValue());
				if (match) {
					adaptersRequiredByActivator.add(adapter);
				}
			}
		}
		return adaptersRequiredByActivator;
	}

	public void setConditional(final String dataPoint) {
		conditionalState.add(dataPoint);
	}

}
