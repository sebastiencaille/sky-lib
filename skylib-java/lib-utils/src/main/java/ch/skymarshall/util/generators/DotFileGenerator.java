package ch.skymarshall.util.generators;

import java.io.IOException;

import ch.skymarshall.util.text.TextFormatter;

public class DotFileGenerator extends TextFormatter<DotFileGenerator> {

	public enum Shape {
		SQUARE, BOX, ELLIPSE, DIAMOND, CIRCLE, POINT, HEXAGON, OCTAGON
	}

	public DotFileGenerator() {
		super(TextFormatter.output(new StringBuilder()));
	}

	public DotFileGenerator header(final String graphName, final String comment) throws IOException {
		appendIndented("// ").append(comment).newLine();
		appendIndented("digraph \"").append(graphName).append("\" {").newLine();
		indent();
		return this;
	}

	public DotFileGenerator straightLines() throws IOException {
		appendIndentedLine("splines=line;");
		return this;
	}

	public DotFileGenerator polyLines() throws IOException {
		appendIndentedLine("splines=polyline;");
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

	public DotFileGenerator addEdge(final String from, final String to, final String label, final boolean isXLabel,
			final String extra) throws IOException {
		String formatted;
		if (label == null || label.isEmpty()) {
			formatted = String.format("\"%s\" -> \"%s\" [ %s ];", from, to, extra);
		} else if (isXLabel) {
			formatted = String.format("\"%s\" -> \"%s\" [ xlabel=\"%s\" %s ];", from, to, escape(label), extra);
		} else {
			formatted = String.format("\"%s\" -> \"%s\" [ label=\"%s\" %s ];", from, to, escape(label), extra);
		}
		appendIndented(formatted).newLine();
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
