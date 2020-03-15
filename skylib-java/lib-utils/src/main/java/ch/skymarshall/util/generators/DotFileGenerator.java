package ch.skymarshall.util.generators;

import java.io.IOException;

import ch.skymarshall.util.text.TextFormatter;

public class DotFileGenerator extends TextFormatter {

	public enum Shape {
		SQUARE, BOX, ELLIPSE
	}

	public DotFileGenerator() {
		super(TextFormatter.output(new StringBuilder()));
	}

	public DotFileGenerator header(final String graphName, final String comment) throws IOException {
		appendIndented("// ").add(comment).newLine();
		appendIndented("digraph \"").append(graphName).append("\" {").newLine();
		indent();
		return this;
	}

	public DotFileGenerator addNode(final String name, final String label, final Shape shape, final String color)
			throws IOException {
		final String extra;
		if (color != null) {
			extra = ", fillcolor=" + color + ", style=filled";
		} else {
			extra = "";
		}
		appendIndented(String.format("\"%s\" [ label=\"%s\", shape=\"%s\" %s ];", escape(name), escape(label),
				shape.name().toLowerCase(), extra)).newLine();
		return this;
	}

	public DotFileGenerator addEdge(final String from, final String to, final String label, final String extra)
			throws IOException {
		appendIndented(String.format("\"%s\" -> \"%s\" [ label=\"%s\" %s ];", from, to, escape(label), extra))
				.newLine(); // NOSONAR
		return this;
	}

	public void footer() throws IOException {
		unindent();
		appendIndented("}");
	}

	public static String escape(final String str) {
		if (str == null) {
			return "";
		}
		return str.replace("\"", "\\\"");
	}

}
