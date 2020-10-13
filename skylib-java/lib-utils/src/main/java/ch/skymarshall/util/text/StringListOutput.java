package ch.skymarshall.util.text;

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
		String[] split = str.split("\n");
		boolean first = true;
		for (String line : split) {
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
		if (currentLine.length() == 0) {
			return buffer;
		}
		final ArrayList<String> fixed = new ArrayList<>(buffer);
		fixed.ensureCapacity(buffer.size() + 1);
		fixed.add(currentLine.toString());
		return fixed;
	}

}