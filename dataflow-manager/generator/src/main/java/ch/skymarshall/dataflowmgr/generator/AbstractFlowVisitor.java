package ch.skymarshall.dataflowmgr.generator;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import ch.skymarshall.dataflowmgr.model.Binding;
import ch.skymarshall.dataflowmgr.model.BindingRule;
import ch.skymarshall.dataflowmgr.model.Condition;
import ch.skymarshall.dataflowmgr.model.ExternalAdapter;
import ch.skymarshall.dataflowmgr.model.Flow;
import ch.skymarshall.dataflowmgr.model.Processor;

public abstract class AbstractFlowVisitor {

	public static class BindingContext {

		public final Binding binding;
		public final List<ExternalAdapter> adapters;
		public final Set<ExternalAdapter> processedAdapters;
		public final List<Condition> activators;

		public final String inputDataPoint;
		public final String inputDataType;
		public final String outputDataPoint;

		public BindingContext(final Binding binding, final String inputDataType) {
			this.binding = binding;
			adapters = binding.getAdapters();
			processedAdapters = new HashSet<>();
			activators = BindingRule.getActivators(binding.getRules()).collect(Collectors.toList());
			inputDataPoint = binding.fromDataPoint();
			this.inputDataType = inputDataType;
			outputDataPoint = binding.toDataPoint();
		}

		public Set<ExternalAdapter> unprocessedAdapters(final Collection<ExternalAdapter> adapters) {
			final Set<ExternalAdapter> unprocessed = new HashSet<>(adapters);
			adapters.removeAll(processedAdapters);
			return unprocessed;
		}

	}

	protected final Flow flow;
	private final Map<Binding, Set<Binding>> currentDeps;
	private final Set<String> conditionalState = new HashSet<>();
	private final Set<Binding> untriggeredBindings;
	private final Map<String, String> availableDataPoints = new HashMap<>();

	protected abstract void process(BindingContext context, Processor processor) throws IOException;

	public AbstractFlowVisitor(final Flow flow) {
		this.flow = flow;
		this.currentDeps = flow.cloneDependencies();
		this.untriggeredBindings = new HashSet<>(flow.getBindings());
	}

	protected boolean isConditional(final String input) {
		return conditionalState.contains(input);
	}

	protected void processFlow() throws IOException {
		availableDataPoints.put(Flow.ENTRY_POINT, flow.getEntryPointType());
		while (!untriggeredBindings.isEmpty()) {
			// More to process
			final Set<Binding> newlyTriggeredBindings = triggerBindings();
			untriggeredBindings.removeAll(newlyTriggeredBindings);
			// make data point available only if all corresponding bindings have been
			// executed
			for (final Binding binding : newlyTriggeredBindings) {
				if (untriggeredBindings.stream().noneMatch(b -> binding.toDataPoint().equals(b.toDataPoint()))) {
					availableDataPoints.put(binding.toDataPoint(), binding.toProcessor().getReturnType());
				}
			}
			currentDeps.values().forEach(v -> v.removeAll(newlyTriggeredBindings));
		}
	}

	private Set<Binding> triggerBindings() throws IOException {
		final Set<Binding> newlyTriggeredBindings = new HashSet<>();
		for (final Binding binding : untriggeredBindings) {
			// Next potential binding
			final Set<Binding> bindingDeps = currentDeps.get(binding);
			final boolean depsTriggered = bindingDeps == null || bindingDeps.isEmpty();
			if (!availableDataPoints.containsKey(binding.fromDataPoint()) || !depsTriggered) {
				continue;
			}

			// Process
			final Processor processor = binding.toProcessor();

			newlyTriggeredBindings.add(binding);
			final BindingContext context = new BindingContext(binding,
					availableDataPoints.get(binding.fromDataPoint()));
			process(context, processor);
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
	protected Set<ExternalAdapter> listAdapters(final BindingContext context, final Condition activator) {
		final HashSet<ExternalAdapter> adaptersRequiredByActivator = new HashSet<>();
		for (final Entry<String, String> param : activator.getParameters().entrySet()) {
			for (final ExternalAdapter adapter : context.adapters) {
				final boolean match = adapter.getName().endsWith('.' + param.getKey())
						|| adapter.getReturnType().equals(param.getValue());
				if (match) {
					adaptersRequiredByActivator.add(adapter);
				}
			}
		}
		return adaptersRequiredByActivator;
	}

	protected void setConditional(final String parameter) {
		conditionalState.add(parameter);
	}

}
