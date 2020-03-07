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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ch.skymarshall.dataflowmgr.generator.AbstractFlowVisitor;
import ch.skymarshall.dataflowmgr.model.Flow;
import ch.skymarshall.dataflowmgr.model.Processor;
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
		String activator;

		public Link(final String from, final String to, final String label, final String activator) {
			this.from = from;
			this.to = to;
			this.label = label;
			this.activator = activator;
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
	}

	@Override
	protected void process(final String processorName, final Processor processor, final String inputParameter) {
		if (!graph.nodes.containsKey(processorName)) {
			graph.nodes.put(processorName, new Node(processorName, processor.getCall(),
					ch.skymarshall.util.generators.DotFileGenerator.Shape.BOX));
		}
		if (inputParameter.equals("input")) {
			return;
		}
		graph.links.add(new Link(inputParameter, processorName, "", ""));
	}

	public DotFileGenerator process() throws IOException {

		super.processFlow();

		try {
			final DotFileGenerator generator = new DotFileGenerator();
			generator.header(flow.getName(), "TBD");

			for (final Node node : graph.nodes.values()) {
				final String color = computeColor(node);
				final String label = node.label;
				final ch.skymarshall.util.generators.DotFileGenerator.Shape shape = node.shape;
				generator.addNode(node.name.toString(), label, shape, color);
			}
			for (final Link link : graph.links) {
				generator.addLink(link.from.toString(), link.to.toString(), link.label + '\n' + link.activator);
			}
			generator.footer();
			return generator;

		} catch (final IOException e) {
			throw new IllegalStateException("Unable to generate DOT", e);
		}
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