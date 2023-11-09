package ch.scaille.util.text;

public class CharIndentationManager implements IIndentationManager {

	private final String indentationLevel;

	private String indentation = "";

	public CharIndentationManager() {
		this(' ', 4);
	}

	public CharIndentationManager(final char c, final int length) {
		final var builder = new StringBuilder();
		for (int i = 0; i < length; i++) {
			builder.append(c);
		}
		indentationLevel = builder.toString();
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
