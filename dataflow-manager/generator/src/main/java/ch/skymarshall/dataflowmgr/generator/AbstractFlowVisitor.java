package ch.skymarshall.dataflowmgr.generator;

import java.io.IOException;
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

	protected abstract void process(Binding binding, String inputDataPoint, Processor processor, String outputDataPoint)
			throws IOException;

	public AbstractFlowVisitor(final Flow flow) {
		this.flow = flow;
		this.currentDeps = flow.cloneDependencies();
	}

	protected boolean isConditional(final String input) {
		return conditionalState.contains(input);
	}

	protected void processFlow() throws IOException {

		final Set<Binding> untriggeredBindings = new HashSet<>(flow.getBindings());
		final Set<String> availableDataPoints = new HashSet<>();

		availableDataPoints.add(Flow.INITIAL_DATAPOINT);
		while (!untriggeredBindings.isEmpty()) {
			// More to process

			final Set<Binding> newlyTriggeredBindings = new HashSet<>();
			for (final Binding binding : untriggeredBindings) {
				// Next potential binding
				final Set<Binding> bindingDeps = currentDeps.get(binding);
				final boolean depsTriggered = bindingDeps == null || bindingDeps.isEmpty();
				LOGGER.fine(() -> "Testing " + binding.fromDataPoint() + " -> " + binding.toProcessor() + ": deps="
						+ depsTriggered);
				if (!availableDataPoints.contains(binding.fromDataPoint()) || !depsTriggered) {
					continue;
				}

				// Process
				final boolean conditional = isConditional(binding.fromDataPoint());
				LOGGER.info(() -> "Handling " + binding.fromDataPoint() + " -> " + binding.toProcessor() + ": cond="
						+ conditional + ", rules=" + binding.getRules());
				final Processor nextProcessor = binding.toProcessor();

				newlyTriggeredBindings.add(binding);
				process(binding, binding.fromDataPoint(), nextProcessor, binding.outputName());
				availableDataPoints.add(binding.outputName());
			}
			if (!untriggeredBindings.isEmpty() && newlyTriggeredBindings.isEmpty()) {
				throw new IllegalStateException("Remaining bindings cannot be processed: " + untriggeredBindings);
			}
			untriggeredBindings.removeAll(newlyTriggeredBindings);
			currentDeps.values().forEach(v -> v.removeAll(newlyTriggeredBindings));
		}
	}

	protected void setConditional(final String parameter) {
		conditionalState.add(parameter);
	}

}
