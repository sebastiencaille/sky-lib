package ch.skymarshall.dataflowmgr.generator.writers.dot;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.skymarshall.util.text.TextFormatter;

import ch.skymarshall.dataflowmgr.generator.model.ActionPoint;
import ch.skymarshall.dataflowmgr.generator.model.Flow;
import ch.skymarshall.dataflowmgr.generator.model.InFlowRule;
import ch.skymarshall.dataflowmgr.generator.model.Module;
import ch.skymarshall.dataflowmgr.generator.model.OutFlowRule;
import ch.skymarshall.dataflowmgr.generator.writers.AbstractWriter;
import ch.skymarshall.dataflowmgr.generator.writers.ModuleVisitor;

public class DotModuleVisitor extends ModuleVisitor<DotModuleVisitor.Graph> {

	private final AbstractWriter writer;

	private static class Node {
		final UUID uuid;
		final String label;
		final Shape shape;

		public Node(final UUID uuid, final String label, final Shape shape) {
			this.uuid = uuid;
			this.label = label;
			this.shape = shape;
		}

	}

	private static class Link {
		final UUID from;
		final UUID to;
		final String label;

		public Link(final UUID from, final UUID to, final String label) {
			this.from = from;
			this.to = to;
			this.label = label;
		}

	}

	public static class Graph {
		final List<Node> nodes = new ArrayList<>();
		final List<Link> links = new ArrayList<>();
		final Set<UUID> executed = new HashSet<>();
		final Set<UUID> expected = new HashSet<>();
	}

	public DotModuleVisitor(final Module module, final AbstractWriter writer) {
		super(module, writer);
		this.writer = writer;
	}

	@Override
	public Graph visit(final Module module, final ActionPoint ap, final InFlowRule rule, final Graph context) {
		context.nodes.add(new Node(rule.uuid, rule.input + "\\n" + rule.activator, Shape.box));
		context.links.add(new Link(rule.uuid, ap.uuid, ap.input));
		return super.visit(module, ap, rule, context);
	}

	@Override
	public Graph visit(final Module module, final ActionPoint ap, final OutFlowRule rule, final Graph context) {
		context.links.add(new Link(ap.uuid, rule.uuid, ap.output));

		context.nodes.add(new Node(rule.uuid, rule.output + "\\n" + rule.activator, Shape.box));
		context.links.add(new Link(rule.uuid, findAction(module, rule.nextAction).uuid, rule.output));
		return super.visit(module, ap, rule, context);
	}

	@Override
	public Graph visit(final Module module, final ActionPoint ap, final Graph context) {
		final Graph c = super.visit(module, ap, context);
		c.nodes.add(new Node(ap.uuid, ap.name, Shape.ellipse));
		return c;
	}

	@Override
	public Graph visit(final Module module, final Flow flow, final Graph context) {
		super.visit(module, flow, context);

		final boolean compare = !context.expected.isEmpty();
		try {
			final TextFormatter output = new TextFormatter(TextFormatter.output(new StringBuilder()));
			output.appendIndented("digraph \"").append(flow.name).append("\" {").newLine();
			output.indent();
			for (final Node node : context.nodes) {
				String color = null;
				if (!compare && context.executed.contains(node.uuid)) {
					color = "yellow";
				} else if (compare) {
					final boolean executed = context.executed.contains(node.uuid);
					final boolean expected = context.expected.contains(node.uuid);
					if (executed && expected) {
						color = "green";
					} else if (executed && !expected) {
						color = "red";
					} else if (!executed && expected) {
						color = "blue";
					} else {
						color = null;
					}
				}
				final String extra;
				if (color != null) {
					extra = ", fillcolor=" + color + ", style=filled";
				} else {
					extra = "";
				}
				output.appendIndented(String.format("\"%s\" [ label=\"%s\", shape=\"%s\" %s ];", node.uuid.toString(),
						node.label, node.shape.name(), extra)).newLine();
			}
			for (final Link link : context.links) {
				output.appendIndented(
						String.format("\"%s\" -> \"%s\" [ label=\"%s\" ];", link.from, link.to, link.label)).newLine();
			}
			output.unindent();
			output.appendIndented("}");

			Files.write(new File(writer.getModuleLocation(module), flow.name + ".dot").toPath(),
					output.getOutput().toString().getBytes("UTF-8"));
		} catch (final IOException e) {
			throw new IllegalStateException("Unable to write file", e);
		}
		return context;
	}

}
