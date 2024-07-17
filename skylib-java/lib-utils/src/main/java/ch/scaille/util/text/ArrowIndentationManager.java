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
		indentation = String.valueOf(c).repeat(Math.max(0, length));
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
