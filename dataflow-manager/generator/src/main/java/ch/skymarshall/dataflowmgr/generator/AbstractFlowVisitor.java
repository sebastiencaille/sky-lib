package ch.skymarshall.dataflowmgr.generator;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import ch.skymarshall.dataflowmgr.model.Binding;
import ch.skymarshall.dataflowmgr.model.BindingRule;
import ch.skymarshall.dataflowmgr.model.Flow;
import ch.skymarshall.dataflowmgr.model.Processor;

public abstract class AbstractFlowVisitor {

	private static final Logger LOGGER = Logger.getLogger(AbstractFlowVisitor.class.getName());

	protected final Flow flow;
	private final Map<Binding, Set<Binding>> currentDeps;
	private final Set<String> conditionalState = new HashSet<>();

	protected abstract void process(String inputParameter, Processor processor, String outputParameter,
			Set<BindingRule> rules) throws IOException;

	public AbstractFlowVisitor(final Flow flow) {
		this.flow = flow;
		this.currentDeps = flow.cloneDependencies();
	}

	protected boolean isConditional(final String input) {
		return conditionalState.contains(input);
	}

	protected void processFlow() throws IOException {

		process("input", flow.getEntryProcessor(), Flow.ENTRY_PROCESSOR, Collections.emptySet());

		final Set<Binding> untriggeredBindings = new HashSet<>(flow.getBindings());
		final Set<String> executedProcessors = new HashSet<>();

		executedProcessors.add(Flow.ENTRY_PROCESSOR);
		while (!untriggeredBindings.isEmpty()) {
			// More to process

			final Set<Binding> newlyTriggeredBindings = new HashSet<>();
			for (final Binding binding : untriggeredBindings) {
				// Next potential binding
				final Set<Binding> bindingDeps = currentDeps.get(binding);
				final boolean depsTriggered = bindingDeps == null || bindingDeps.isEmpty();
				LOGGER.fine(() -> "Testing " + binding.fromProcessor() + " -> " + binding.toProcessor() + ": deps="
						+ depsTriggered);
				if (!executedProcessors.contains(binding.fromProcessor()) || !depsTriggered) {
					continue;
				}

				// Process
				final boolean conditional = isConditional(binding.fromProcessor());
				LOGGER.info(() -> "Handling " + binding.fromProcessor() + " -> " + binding.toProcessor() + ": cond="
						+ conditional + ", rules=" + binding.getRules());
				final Processor nextProcessor = flow.getProcessor(binding.toProcessor());

				newlyTriggeredBindings.add(binding);
				process(binding.fromProcessor(), nextProcessor, binding.toProcessor(), binding.getRules());
				executedProcessors.add(binding.toProcessor());
			}
			untriggeredBindings.removeAll(newlyTriggeredBindings);
			currentDeps.values().forEach(v -> v.removeAll(newlyTriggeredBindings));
		}
	}

	protected void setConditional(final String parameter) {
		conditionalState.add(parameter);
	}

}
