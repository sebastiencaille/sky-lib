package ch.scaille.util.text;

import java.util.ArrayList;
import java.util.List;

public class StringListOutput implements TextFormatter.IOutput<RuntimeException> {

	private final List<String> buffer = new ArrayList<>();
	private StringBuilder currentLine = new StringBuilder();

	private void push() {
		buffer.add(currentLine.toString());
		currentLine = new StringBuilder();
	}

	@Override
	public void append(String str) {
		if (str.startsWith("\n")) {
			push();
		}
		final var split = str.split("\n");
		var first = true;
		for (final var line : split) {
			currentLine.append(line);
			if (!first) {
				push();
			}
			first = false;
		}
		if (str.endsWith("\n")) {
			push();
		}
	}

	@Override
	public void append(char c) {
		if (c == '\n') {
			push();
		} else {
			currentLine.append(c);
		}
	}

	public List<String> getLines() {
		if (currentLine.isEmpty()) {
			return buffer;
		}
		final var fixed = new ArrayList<>(buffer);
		fixed.ensureCapacity(buffer.size() + 1);
		fixed.add(currentLine.toString());
		return fixed;
	}

}