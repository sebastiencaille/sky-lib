package ch.skymarshall.dataflowmgr.generator.writers.dot;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

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
		final String name;
		final Shape shape;

		public Node(final String name, final Shape shape) {
			super();
			this.name = name;
			this.shape = shape;
		}

	}

	private static class Link {
		final String from;
		final String to;
		final String label;

		public Link(final String from, final String to, final String label) {
			super();
			this.from = from;
			this.to = to;
			this.label = label;
		}

	}

	public static class Graph {
		final List<Node> nodes = new ArrayList<>();
		final List<Link> links = new ArrayList<>();
	}

	public DotModuleVisitor(final Module module, final AbstractWriter writer) {
		super(module, writer);
		this.writer = writer;
	}

	@Override
	public Graph visit(final Module module, final ActionPoint ap, final InFlowRule rule, final Graph context) {
		context.nodes.add(new Node(rule.uuid.toString(), Shape.box));
		context.links.add(new Link(rule.uuid.toString(), ap.name, ap.input));
		return super.visit(module, ap, rule, context);
	}

	@Override
	public Graph visitField(final Module module, final ActionPoint ap, final OutFlowRule rule, final Graph context) {
		context.links.add(new Link(ap.name, rule.uuid.toString(), ap.output));
		context.nodes.add(new Node(rule.uuid.toString(), Shape.box));
		context.links.add(new Link(rule.uuid.toString(), rule.nextAction, rule.output));
		return super.visitField(module, ap, rule, context);
	}

	@Override
	public Graph visit(final Module module, final Flow flow, final Graph context) {
		super.visit(module, flow, context);
		final StringBuilder output = new StringBuilder("digraph \"").append(flow.name).append("\" {\n");
		for (final Node node : context.nodes) {
			output.append(String.format("	\"%s\" [ shape=\"%s\" ];\n", node.name, node.shape.name()));
		}
		for (final Link link : context.links) {
			output.append(String.format("	\"%s\" -> \"%s\" [ label=\"%s\" ];\n", link.from, link.to, link.label));
		}
		output.append("}");
		try {
			Files.write(new File(writer.getModuleLocation(module), flow.name + ".dot").toPath(),
					output.toString().getBytes("UTF-8"));
		} catch (final IOException e) {
			throw new IllegalStateException("Unable to write file", e);
		}
		return context;
	}

}
