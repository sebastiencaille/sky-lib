package ch.scaille.util.text;

public class CharIndentationManager implements IIndentationManager {

	private final String indentationLevel;

	private String indentation = "";

	public CharIndentationManager() {
		this(' ', 4);
	}

	public CharIndentationManager(final char c, final int length) {
		indentationLevel = String.valueOf(c).repeat(Math.max(0, length));
	}

	@Override
	public void indent() {
		indentation += indentationLevel;
	}

	@Override
	public void unindent() {
		indentation = indentation.substring(indentationLevel.length());
	}

	@Override
	public String getIndentation() {
		return indentation;
	}

}
