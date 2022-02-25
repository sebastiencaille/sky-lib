package ch.scaille.dataflowmgr.generator.writers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ch.scaille.dataflowmgr.model.Binding;
import ch.scaille.dataflowmgr.model.Call;
import ch.scaille.dataflowmgr.model.ExternalAdapter;
import ch.scaille.dataflowmgr.model.Flow;
import ch.scaille.dataflowmgr.model.Processor;

public abstract class AbstractFlowVisitor {

	public static class BindingContext {

		public final Binding binding;
		public final List<ExternalAdapter> bindingAdapters;
		public final Set<ExternalAdapter> processedAdapters;

		public final String inputDataPoint;
		public final String inputDataType;
		public final String outputDataPoint;
		private List<Binding> reverseDeps;

		public BindingContext(final Binding binding, final String inputDataType) {
			this.binding = binding;
			bindingAdapters = binding.getAdapters();
			processedAdapters = new HashSet<>();
			inputDataPoint = binding.fromDataPoint();
			this.inputDataType = inputDataType;
			outputDataPoint = binding.toDataPoint();
		}

		public Set<ExternalAdapter> unprocessedAdapters(final Collection<ExternalAdapter> adapters) {
			final var unprocessed = new HashSet<>(adapters);
			adapters.removeAll(processedAdapters);
			return unprocessed;
		}

		public List<Binding> getReverseDeps() {
			return reverseDeps;
		}

		public void setReverseDeps(final List<Binding> reverseDeps) {
			this.reverseDeps = reverseDeps;
		}

		public boolean isExit() {
			return Flow.EXIT_PROCESSOR.equals(outputDataPoint);
		}

		public Processor getProcessor() {
			return binding.getProcessor();
		}

	}

	protected final Flow flow;
	private final Map<Binding, Set<Binding>> missingDeps;
	private final Set<String> conditionalState = new HashSet<>();
	private final Set<Binding> untriggeredBindings;
	private final Map<String, String> availableDataPoints = new HashMap<>();
	private final Map<Binding, List<Binding>> reverseDeps = new HashMap<>();
	protected final List<BindingContext> processOrder = new ArrayList<>();

	protected abstract void process(BindingContext context);

	protected AbstractFlowVisitor(final Flow flow) {
		this.flow = flow;
		this.missingDeps = flow.cloneDependencies();
		this.untriggeredBindings = new HashSet<>(flow.getBindings());
	}

	public boolean isConditionalData(final String input) {
		return conditionalState.contains(input);
	}

	protected void processFlow() {
		availableDataPoints.put(Flow.ENTRY_POINT, flow.getEntryPointType());
		while (!untriggeredBindings.isEmpty()) {
			// More to process
			final var newlyTriggeredBindings = getTriggeredBindings();
			untriggeredBindings.removeAll(newlyTriggeredBindings);
			// make data point available only if all corresponding bindings have been
			// executed
			for (final var binding : newlyTriggeredBindings) {
				if (untriggeredBindings.stream().noneMatch(b -> binding.toDataPoint().equals(b.toDataPoint()))) {
					availableDataPoints.put(binding.toDataPoint(), binding.getProcessor().getReturnType());
				}
			}
			missingDeps.values().forEach(v -> v.removeAll(newlyTriggeredBindings));
		}

		for (final var c : processOrder) {
			c.setReverseDeps(reverseDeps.getOrDefault(c.binding, Collections.emptyList()));
			process(c);
		}
	}

	private Set<Binding> getTriggeredBindings() {
		final var newlyTriggeredBindings = new HashSet<Binding>();
		for (final var binding : untriggeredBindings) {
			// Next potential binding
			final Set<Binding> bindingDeps = missingDeps.get(binding);
			final boolean depsTriggered = bindingDeps == null || bindingDeps.isEmpty();
			if (!availableDataPoints.containsKey(binding.fromDataPoint()) || !depsTriggered) {
				continue;
			}

			// Process
			final var context = new BindingContext(binding, availableDataPoints.get(binding.fromDataPoint()));
			processOrder.add(context);
			for (final var dep : flow.getAllDependencies(binding)) {
				reverseDeps.computeIfAbsent(dep, v -> new ArrayList<>()).add(context.binding);
			}

			newlyTriggeredBindings.add(binding);
		}
		if (!untriggeredBindings.isEmpty() && newlyTriggeredBindings.isEmpty()) {
			throw new IllegalStateException("Remaining bindings cannot be processed: " + untriggeredBindings);
		}
		return newlyTriggeredBindings;
	}

	/**
	 * Lists the adapters required by the activators
	 *
	 * @param activator
	 * @param undeclaredAdapters
	 * @return
	 */
	public Set<ExternalAdapter> listAdapters(final BindingContext context, final Call<?> call) {
		final var adaptersRequiredByActivator = new HashSet<ExternalAdapter>();
		for (final var param : call.getParameters().entrySet()) {
			for (final var adapter : context.bindingAdapters) {
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
