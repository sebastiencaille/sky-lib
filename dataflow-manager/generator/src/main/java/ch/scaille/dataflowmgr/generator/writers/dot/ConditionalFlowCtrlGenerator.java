package ch.scaille.dataflowmgr.generator.writers.dot;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import ch.scaille.dataflowmgr.generator.writers.AbstractFlowVisitor.CallContext;
import ch.scaille.dataflowmgr.generator.writers.dot.FlowToDotVisitor.Graph;
import ch.scaille.dataflowmgr.generator.writers.dot.FlowToDotVisitor.Link;
import ch.scaille.dataflowmgr.generator.writers.dot.FlowToDotVisitor.Node;
import ch.scaille.dataflowmgr.model.Call;
import ch.scaille.dataflowmgr.model.GenericCall;
import ch.scaille.dataflowmgr.model.flowctrl.ConditionalFlowCtrl;
import ch.scaille.generators.util.DotFileGenerator;

public class ConditionalFlowCtrlGenerator extends AbstractDotFlowGenerator {

	protected ConditionalFlowCtrlGenerator(FlowToDotVisitor visitor, Graph graph) {
		super(visitor, graph);
	}

	@Override
	public boolean matches(CallContext context) {
		return ConditionalFlowCtrl.getCondition(context.processor.getRules()).isPresent();
	}

	@Override
	public void generate(BaseGenContext<String> genContext, CallContext context) {

		final var conditionalCtrl = ConditionalFlowCtrl.getCondition(context.processor.getRules())
				.orElseThrow(() -> new IllegalStateException("Unable to find conditional flow"));

		final var conditionNodeName = getConditionGroupNodeName(conditionalCtrl);
		if (!graph.nodes().containsKey(conditionNodeName)) {
			addConditionGroup(conditionalCtrl);
			graph.links().add(new Link(genContext.getLocalContext(), conditionNodeName, "", ""));
		}
		var nextLink = conditionNodeName;

		final var activators = new ArrayList<>(
				ConditionalFlowCtrl.getActivators(context.processor.getRules()).toList());
		if (activators.isEmpty()) {
			activators.add(new GenericCall("Default", "Default", new LinkedHashMap<>(), Boolean.TYPE.toString()));
		}
		for (final var activator : activators) {
			final var activatorNode = addCondition(activator);
			final var missingAdapters = context.unprocessedAdapters(visitor.listAdapters(context, activator));
			visitor.addExternalAdapters(missingAdapters, nextLink, activatorNode);
			context.processedAdapters.addAll(missingAdapters);
			nextLink = activatorNode;
		}
		genContext.setLocalContext(nextLink);
		genContext.run(context);
	}

	private String getConditionGroupNodeName(final ConditionalFlowCtrl group) {
		return "condGrp_" + visitor.toVar(group);
	}

	private String getConditionNodeName(final Call cond) {
		return "cond_" + visitor.toVar(cond);
	}

	private String addConditionGroup(final ConditionalFlowCtrl group) {
		final var condGroupNode = getConditionGroupNodeName(group);
		graph.nodes().put(condGroupNode, new Node(condGroupNode, group.getName(), "$ ?", DotFileGenerator.Shape.DIAMOND));
		return condGroupNode;
	}

	private String addCondition(final Call condition) {
		final var condNode = getConditionNodeName(condition);
		graph.nodes().put(condNode, new Node(condNode, condition.getCall(), "$: true", DotFileGenerator.Shape.OCTAGON));
		return condNode;
	}

}
