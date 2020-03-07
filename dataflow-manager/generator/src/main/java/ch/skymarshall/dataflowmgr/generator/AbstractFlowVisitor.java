package ch.skymarshall.dataflowmgr.generator;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import ch.skymarshall.dataflowmgr.model.Binding;
import ch.skymarshall.dataflowmgr.model.Flow;
import ch.skymarshall.dataflowmgr.model.Processor;

public abstract class AbstractFlowVisitor {
	protected final Flow flow;

	protected abstract void process(String processorName, Processor processor, String inputParameter)
			throws IOException;

	public AbstractFlowVisitor(final Flow flow) {
		this.flow = flow;
	}

	protected void processFlow() throws IOException {

		process(Flow.ENTRY_PROCESSOR, flow.getProcessor(Flow.ENTRY_PROCESSOR), "input");

		final Set<Binding> untriggeredBindings = new HashSet<>(flow.getBindings());
		final Set<String> executedProcessors = new HashSet<>();
		executedProcessors.add(Flow.ENTRY_PROCESSOR);
		while (!untriggeredBindings.isEmpty()) {
			final Set<Binding> newlyTriggeredBindings = new HashSet<>();
			for (final Binding binding : untriggeredBindings) {
				if (executedProcessors.contains(binding.fromProcessor())) {
					newlyTriggeredBindings.add(binding);
					process(binding.toProcessor(), flow.getProcessor(binding.toProcessor()), binding.fromProcessor());
					executedProcessors.add(binding.toProcessor());
				}
			}
			untriggeredBindings.removeAll(newlyTriggeredBindings);
		}
	}

}
