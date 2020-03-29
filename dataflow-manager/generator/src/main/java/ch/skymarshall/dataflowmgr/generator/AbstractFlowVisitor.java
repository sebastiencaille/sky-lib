package ch.skymarshall.dataflowmgr.generator;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import ch.skymarshall.dataflowmgr.model.Binding;
import ch.skymarshall.dataflowmgr.model.Flow;
import ch.skymarshall.dataflowmgr.model.Processor;

public abstract class AbstractFlowVisitor {

	private static final Logger LOGGER = Logger.getLogger(AbstractFlowVisitor.class.getName());

	protected final Flow flow;
	private final Map<Binding, Set<Binding>> currentDeps;
	private final Set<String> conditionalState = new HashSet<>();

	protected abstract void process(Binding binding, String inputDataPoint, String inputDataType, Processor processor,
			String outputDataPoint) throws IOException;

	public AbstractFlowVisitor(final Flow flow) {
		this.flow = flow;
		this.currentDeps = flow.cloneDependencies();
	}

	protected boolean isConditional(final String input) {
		return conditionalState.contains(input);
	}

	protected void processFlow() throws IOException {

		final Set<Binding> untriggeredBindings = new HashSet<>(flow.getBindings());
		final Map<String, String> availableDataPoints = new HashMap<>();

		availableDataPoints.put(Flow.ENTRY_POINT, flow.getEntryPointType());
		while (!untriggeredBindings.isEmpty()) {
			// More to process

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
				process(binding, binding.fromDataPoint(), availableDataPoints.get(binding.fromDataPoint()), processor,
						binding.outputName());
			}
			if (!untriggeredBindings.isEmpty() && newlyTriggeredBindings.isEmpty()) {
				throw new IllegalStateException("Remaining bindings cannot be processed: " + untriggeredBindings);
			}
			untriggeredBindings.removeAll(newlyTriggeredBindings);
			// make datapoint available only if all corresponding bindings have been
			// executed
			for (final Binding binding : newlyTriggeredBindings) {
				if (untriggeredBindings.stream().noneMatch(b -> binding.outputName().equals(b.outputName()))) {
					availableDataPoints.put(binding.outputName(), binding.toProcessor().getReturnType());
				}
			}

			currentDeps.values().forEach(v -> v.removeAll(newlyTriggeredBindings));
		}

	}

	protected void setConditional(final String parameter) {
		conditionalState.add(parameter);
	}

}
