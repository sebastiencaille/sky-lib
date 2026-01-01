package ch.scaille.generators.util;

import ch.scaille.util.text.TextFormatter;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public class DotFileGenerator<E extends Exception> extends TextFormatter<DotFileGenerator<E>, E> {

	public enum Shape {
		SQUARE, BOX, ELLIPSE, DIAMOND, CIRCLE, POINT, HEXAGON, OCTAGON
	}

	public static DotFileGenerator<RuntimeException> inMemory() {
		return new DotFileGenerator<>(output(new StringBuilder()));
	}

	public DotFileGenerator(IOutput<E> output) {
		super(output);
	}

	public DotFileGenerator<E> header(final String graphName, final String comment) throws E {
		appendIndented("// ").append(comment).eol();
		appendIndented("digraph \"").append(graphName).append("\" {").eol();
		indent();
		return this;
	}

	public DotFileGenerator<E> straightLines() throws E {
		appendIndentedLine("splines=line;");
		return this;
	}

	public DotFileGenerator<E> polyLines() throws E {
		appendIndentedLine("splines=polyline;");
		return this;
	}

	public DotFileGenerator<E> addNode(final String name, final String label, final Shape shape, @Nullable final String color)
			throws E {
		final String extra;
		if (color != null) {
			extra = ", fillcolor=" + color + ", style=filled";
		} else {
			extra = "";
		}
		appendIndented(String.format("\"%s\" [ label=\"%s\", shape=\"%s\" %s ];", escape(name), escape(label),
				shape.name().toLowerCase(), extra)).eol();
		return this;
	}

	public DotFileGenerator<E> addEdge(final String from, final String to, @Nullable final String label, final boolean isXLabel,
									   final String extra) throws E {
		final String formatted;
		if (label == null || label.isEmpty()) {
			formatted = String.format("\"%s\" -> \"%s\" [ %s ];", from, to, extra);
		} else if (isXLabel) {
			formatted = String.format("\"%s\" -> \"%s\" [ xlabel=\"%s\" %s ];", from, to, escape(label), extra);
		} else {
			formatted = String.format("\"%s\" -> \"%s\" [ label=\"%s\" %s ];", from, to, escape(label), extra);
		}
		appendIndented(formatted).eol();
		return this;
	}

	public void footer() throws E {
		unindent();
		appendIndented("}");
	}

	public static String escape(@Nullable final String str) {
		if (str == null) {
			return "";
		}
		return str.replace("\"", "\\\"");
	}

}
