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
import ch.scaille.generators.util.DotFileGenerator;

public class FlowToDotVisitor extends AbstractFlowVisitor {

	private final Graph graph;

	public record Node(
		 String name,
		 String label,
		 DotFileGenerator.Shape shape) {

		public Node(final String name, final String label, String enhancer, final DotFileGenerator.Shape shape) {
			this(name, enhancedLabel(label, enhancer), shape);
		}

		private static String enhancedLabel(String aLabel, String enhancer) {
			final var shortLabel = aLabel.substring(aLabel.lastIndexOf('.') + 1);
			return (enhancer != null) ? enhancer.replace("$", shortLabel) : shortLabel;
		}

	}

	public record Link(
		 String from,
		 String to,
		 String label,
		 String extra) {

		public Link(final String from, final String to) {
			this(from, to, "", "");
		}
	}

	public record Graph(
		 Map<String, Node> nodes,
		 List<Link> links,
		 Set<String> executed,
		 Set<String> expected) {

		public Graph() {
			this(new HashMap<>(), new ArrayList<>(), new HashSet<>(), new HashSet<>());
		}
	}

	private final FlowGeneratorVisitor<String> flowGeneratorVisitor = new FlowGeneratorVisitor<>();

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
		for (final var adapter : externalAdapters) {
			final var activatorNodeName = addAdapter(adapter);
			graph.links.add(new Link(linkFrom, activatorNodeName));
			graph.links.add(new Link(activatorNodeName, linkTo));
		}
	}

	private void addDataPoint(final String name) {
		graph.nodes.put(name, new Node(name, "", null, DotFileGenerator.Shape.POINT));
	}

	private String addAdapter(final ExternalAdapter adapter) {
		final var nodeName = toVar(adapter);
		graph.nodes.put(nodeName, new Node(nodeName, adapter.getCall(), "External:$", DotFileGenerator.Shape.BOX));
		return nodeName;
	}

	String addProcessor(final Binding binding, final Processor processor) {
		final var nodeName = toVar(binding) + "_" + processor.getCall().replace('.', '_');
		graph.nodes.put(nodeName, new Node(nodeName, processor.getCall(), null, DotFileGenerator.Shape.ELLIPSE));
		return nodeName;
	}

	String toVar(final WithId withId) {
		return withId.uuid().toString();
	}

	public DotFileGenerator<RuntimeException> process() {

		super.processFlow();

		final var dotGenerator = DotFileGenerator.inMemory();
		dotGenerator.header(flow.getName(), "TBD");
		dotGenerator.polyLines();

		for (final var graphNode : graph.nodes.values()) {
			final var color = computeColor(graphNode);
			final var label = graphNode.label;
			final var shape = graphNode.shape;
			dotGenerator.addNode(graphNode.name, label, shape, color);
		}
		for (final var link : graph.links) {
			dotGenerator.addEdge(link.from, link.to, link.label, false, link.extra);
		}
		dotGenerator.footer();
		return dotGenerator;
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
