package ch.scaille.util.text;

/**
 * Provides indentation with chars followed by an arrow.
 * <p>
 * 
 * @author Sebastien Caille
 * 
 */
public class ArrowIndentationManager implements IIndentationManager {

	private final String indentation;

	private String currentIndentation = "";

	private int level = 0;

	public ArrowIndentationManager() {
		this(' ', 4);
	}

	public ArrowIndentationManager(final char c, final int length) {
		final var builder = new StringBuilder();
		for (int i = 0; i < length; i++) {
			builder.append(c);
		}
		indentation = builder.toString();
	}

	@Override
	public void indent() {
		currentIndentation += indentation;
		level++;
	}

	@Override
	public void unindent() {
		currentIndentation = currentIndentation.substring(indentation.length());
		level--;
	}

	@Override
	public String getIndentation() {
		if (level == 0) {
			return "--> ";
		}
		return currentIndentation;
	}

}
