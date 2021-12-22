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
package ch.scaille.dataflowmgr.generator.writers.dot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ch.scaille.dataflowmgr.generator.writers.AbstractFlowVisitor;
import ch.scaille.dataflowmgr.generator.writers.FlowGeneratorVisitor;
import ch.scaille.dataflowmgr.model.Binding;
import ch.scaille.dataflowmgr.model.ExternalAdapter;
import ch.scaille.dataflowmgr.model.Flow;
import ch.scaille.dataflowmgr.model.Processor;
import ch.scaille.dataflowmgr.model.WithId;
import ch.scaille.util.generators.DotFileGenerator;

public class FlowToDotVisitor extends AbstractFlowVisitor {

	private final Graph graph;

	static class Node {
		final String name;
		final String label;
		final DotFileGenerator.Shape shape;

		public Node(final String name, final String label, String enhancer, final DotFileGenerator.Shape shape) {
			this.name = name;
			String shortLabel = label.substring(label.lastIndexOf('.') + 1);
			this.label = (enhancer != null) ? enhancer.replace("$", shortLabel) : shortLabel;
			this.shape = shape;
		}

	}

	static class Link {
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

	static class Graph {
		final Map<String, Node> nodes = new HashMap<>();
		final List<Link> links = new ArrayList<>();
		final Set<String> executed = new HashSet<>();
		final Set<String> expected = new HashSet<>();
	}

	private FlowGeneratorVisitor<String> flowGeneratorVisitor = new FlowGeneratorVisitor<>();

	public FlowToDotVisitor(final Flow flow) {
		super(flow);
		this.graph = new Graph();
		flowGeneratorVisitor.register(new ConditionalFlowCtrlGenerator(this, graph));
		flowGeneratorVisitor.register(new ProcessorGenerator(this, graph));
		addDataPoint(Flow.ENTRY_POINT);
	}

	@Override
	protected void process(final BindingContext context) {
		// Create data point
		if (!graph.nodes.containsKey(context.outputDataPoint)) {
			addDataPoint(context.outputDataPoint);
		}

		flowGeneratorVisitor.generateFlow(context, context.inputDataPoint);
	}

	void addExternalAdapters(final Set<ExternalAdapter> externalAdapters, final String linkFrom, final String linkTo) {
		if (externalAdapters.isEmpty()) {
			graph.links.add(new Link(linkFrom, linkTo));
			return;
		}
		for (final ExternalAdapter adapter : externalAdapters) {
			final String activatorNodeName = addAdapter(adapter);
			graph.links.add(new Link(linkFrom, activatorNodeName));
			graph.links.add(new Link(activatorNodeName, linkTo));
		}
	}

	private void addDataPoint(final String name) {
		graph.nodes.put(name, new Node(name, "", null, ch.scaille.util.generators.DotFileGenerator.Shape.POINT));
	}

	private String addAdapter(final ExternalAdapter adapter) {
		final String nodeName = toVar(adapter);
		graph.nodes.put(nodeName, new Node(nodeName, adapter.getCall(), "External:$",
				ch.scaille.util.generators.DotFileGenerator.Shape.BOX));
		return nodeName;
	}

	String addProcessor(final Binding binding, final Processor processor) {
		final String nodeName = toVar(binding) + "_" + processor.getCall().replace('.', '_');
		graph.nodes.put(nodeName, new Node(nodeName, processor.getCall(), null,
				ch.scaille.util.generators.DotFileGenerator.Shape.ELLIPSE));
		return nodeName;
	}

	String toVar(final WithId withId) {
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
			final ch.scaille.util.generators.DotFileGenerator.Shape shape = node.shape;
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
