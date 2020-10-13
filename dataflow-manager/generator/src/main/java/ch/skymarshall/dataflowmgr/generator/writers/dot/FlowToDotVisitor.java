/*******************************************************************************
 * Copyright (c) 2017 Sebastien Caille.
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms are permitted
 *  provided that the above Copyrightnotice and this paragraph are
 *  duplicated in all such forms and that any documentation,
 *  advertising materials, and other materials related to such
 *  distribution and use acknowledge that the software was developed
 *  by Sebastien Caille.  The name of Sebastien Caille may not be used to endorse or promote products derived
 *  from this software without specific prior written permission.
 *  THIS SOFTWARE IS PROVIDED ``AS IS'' AND WITHOUT ANY EXPRESS OR
 *  IMPLIED WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 ******************************************************************************/
package ch.skymarshall.dataflowmgr.generator.writers.dot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import ch.skymarshall.dataflowmgr.generator.AbstractFlowVisitor;
import ch.skymarshall.dataflowmgr.model.Binding;
import ch.skymarshall.dataflowmgr.model.BindingRule;
import ch.skymarshall.dataflowmgr.model.Condition;
import ch.skymarshall.dataflowmgr.model.ConditionalBindingGroup;
import ch.skymarshall.dataflowmgr.model.ExternalAdapter;
import ch.skymarshall.dataflowmgr.model.Flow;
import ch.skymarshall.dataflowmgr.model.Processor;
import ch.skymarshall.dataflowmgr.model.WithId;
import ch.skymarshall.util.generators.DotFileGenerator;

public class FlowToDotVisitor extends AbstractFlowVisitor {

	private final Graph graph;

	private static class Node {
		final String name;
		final String label;
		final DotFileGenerator.Shape shape;

		public Node(final String name, final String label, final DotFileGenerator.Shape shape) {
			this.name = name;
			this.label = label;
			this.shape = shape;
		}

	}

	private static class Link {
		final String from;
		final String to;
		final String label;
		final String extra;

		public Link(final String from, final String to) {
			this(from, to, "", "");
		}

		public Link(final String from, final String to, final String label, final String extra) {
			this.from = from;
			this.to = to;
			this.label = label;
			this.extra = extra;
		}

	}

	public static class Graph {
		final Map<String, Node> nodes = new HashMap<>();
		final List<Link> links = new ArrayList<>();
		final Set<String> executed = new HashSet<>();
		final Set<String> expected = new HashSet<>();
	}

	public FlowToDotVisitor(final Flow flow) {
		super(flow);
		this.graph = new Graph();
		addDataPoint(Flow.ENTRY_POINT);
	}

	@Override
	protected void process(final BindingContext context, final Processor processor) {
		// Create data point
		if (!graph.nodes.containsKey(context.outputDataPoint)) {
			addDataPoint(context.outputDataPoint);
		}

		final String processorNode = addProcessor(context.binding, processor);

		final Optional<ConditionalBindingGroup> conditionGroupOpt = BindingRule
				.getAll(context.binding.getRules(), BindingRule.Type.CONDITIONAL, ConditionalBindingGroup.class)
				.findAny();

		// Add condition
		String linkFrom;
		if (conditionGroupOpt.isPresent()) {
			final ConditionalBindingGroup conditionGroup = conditionGroupOpt.get();
			final String conditionNodeName = getConditionGroupNodeName(conditionGroup);
			if (!graph.nodes.containsKey(conditionNodeName)) {
				addConditionGroup(conditionGroup);
				graph.links.add(new Link(context.inputDataPoint, conditionNodeName, "", ""));
			}
			linkFrom = conditionNodeName;
		} else {
			linkFrom = context.inputDataPoint;
		}

		if (conditionGroupOpt.isPresent() && context.activators.isEmpty()) {
			context.activators.add(new Condition("Default", "Default", new LinkedHashMap<>()));
		}
		for (final Condition activator : context.activators) {
			final String activatorNode = addCondition(activator);
			final Set<ExternalAdapter> missingAdapters = context.unprocessedAdapters(listAdapters(context, activator));
			addAdapters(missingAdapters, linkFrom, activatorNode);
			context.processedAdapters.addAll(missingAdapters);
			linkFrom = activatorNode;
		}

		addAdapters(context.processedAdapters, linkFrom, processorNode);

		graph.links.add(new Link(processorNode, context.outputDataPoint));
	}

	private void addAdapters(final Set<ExternalAdapter> adapters, final String linkFrom, final String linkTo) {
		if (adapters.isEmpty()) {
			graph.links.add(new Link(linkFrom, linkTo));
			return;
		}
		for (final ExternalAdapter adapter : adapters) {
			final String activatorNodeName = addAdapter(adapter);
			graph.links.add(new Link(linkFrom, activatorNodeName));
			graph.links.add(new Link(activatorNodeName, linkTo));
		}
	}

	private void addDataPoint(final String name) {
		graph.nodes.put(name, new Node(name, "", ch.skymarshall.util.generators.DotFileGenerator.Shape.POINT));
	}

	private String getConditionGroupNodeName(final ConditionalBindingGroup group) {
		return "condGrp_" + toVar(group);
	}

	private String getConditionNodeName(final Condition cond) {
		return "cond_" + toVar(cond);
	}

	private String addConditionGroup(final ConditionalBindingGroup group) {
		final String condGroupNode = getConditionGroupNodeName(group);
		graph.nodes.put(condGroupNode, new Node(condGroupNode, group.getName(), DotFileGenerator.Shape.DIAMOND));
		return condGroupNode;
	}

	private String addCondition(final Condition condition) {
		final String condNode = getConditionNodeName(condition);
		graph.nodes.put(condNode, new Node(condNode, condition.getCall(), DotFileGenerator.Shape.OCTAGON));
		return condNode;
	}

	private String addAdapter(final ExternalAdapter adapter) {
		final String nodeName = toVar(adapter);
		graph.nodes.put(nodeName,
				new Node(nodeName, adapter.getCall(), ch.skymarshall.util.generators.DotFileGenerator.Shape.ELLIPSE));
		return nodeName;
	}

	private String addProcessor(final Binding binding, final Processor processor) {
		final String nodeName = toVar(binding) + "_" + processor.getCall().replace('.', '_');
		graph.nodes.put(nodeName,
				new Node(nodeName, processor.getCall(), ch.skymarshall.util.generators.DotFileGenerator.Shape.BOX));
		return nodeName;
	}

	private String toVar(final WithId withId) {
		return withId.uuid().toString();
	}

	public DotFileGenerator<RuntimeException> process() {

		super.processFlow();

		final DotFileGenerator<RuntimeException> generator = DotFileGenerator.inMemory();
		generator.header(flow.getName(), "TBD");
		generator.polyLines();

		for (final Node node : graph.nodes.values()) {
			final String color = computeColor(node);
			final String label = node.label;
			final ch.skymarshall.util.generators.DotFileGenerator.Shape shape = node.shape;
			generator.addNode(node.name, label, shape, color);
		}
		for (final Link link : graph.links) {
			generator.addEdge(link.from, link.to, link.label, false, link.extra);
		}
		generator.footer();
		return generator;

	}

	private String computeColor(final Node node) {
		final boolean compare = !graph.expected.isEmpty();
		String color = null;
		if (!compare && graph.executed.contains(node.name)) {
			color = "yellow";
		} else if (compare) {
			final boolean executed = graph.executed.contains(node.name);
			final boolean expected = graph.expected.contains(node.name);
			if (executed && expected) {
				color = "green";
			} else if (executed) {
				color = "red";
			} else if (expected) {
				color = "lightblue";
			}
		}
		return color;
	}

}
