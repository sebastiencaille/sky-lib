package org.skymarshall.util.generators;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class Template {

	private final String content;
	private final Map<String, String> properties = new HashMap<>();

	private String commandLine;

	public Template(final String content) {
		this.content = content.replaceAll("\r", "");
	}

	public Template apply(final Map<String, String> newProperties) {
		final Template newTemplate = create(content);
		newTemplate.setCommandLine(commandLine);
		newTemplate.setContext(newProperties);
		return newTemplate;
	}

	protected Template create(final String newContent) {
		return new Template(newContent);
	}

	public void setCommandLine(final String commandLine) {
		this.commandLine = commandLine;
	}

	public String generate() {
		final StringBuilder result = new StringBuilder("// File generated from template").append("\n");
		if (commandLine != null) {
			result.append("// ").append(commandLine).append("\n");
		}
		int nextVariablePos = 0;
		int currentPos = 0;
		while ((nextVariablePos = content.indexOf("${", nextVariablePos)) > 0) {
			if (nextVariablePos > 0 && content.charAt(nextVariablePos - 1) == '$') {
				// it's a $$, skip
				nextVariablePos++;
				continue;
			}

			final String variable = content.substring(nextVariablePos + 2, content.indexOf('}', nextVariablePos));
			final String value = properties.get(variable);
			if (value == null) {
				throw new TemplateException("No value for property " + variable);
			}
			final String indent = getIndentation(nextVariablePos);
			result.append(content.substring(currentPos, nextVariablePos)).append(value.replaceAll("\n", indent));
			nextVariablePos = nextVariablePos + variable.length() + 3;
			currentPos = nextVariablePos;
		}
		result.append(content.substring(currentPos, content.length()));
		return result.toString();
	}

	private String getIndentation(final int nextVariablePos) {
		int prevEol = nextVariablePos;
		int startOfText = nextVariablePos;
		while (prevEol >= 0 && content.charAt(prevEol) != '\n') {
			if (content.charAt(prevEol) != ' ' && content.charAt(prevEol) != '\t') {
				startOfText = prevEol;
			}
			prevEol--;
		}
		final String indent;
		if (prevEol >= 0 && startOfText > prevEol) {
			indent = content.substring(prevEol, startOfText);
		} else {
			indent = "";
		}
		return indent;
	}

	public void write(final File file) throws IOException {
		Logger.getLogger(Template.class.getName()).info(() -> "Write " + file);
		file.getParentFile().mkdirs();

		try (final FileWriter out = new FileWriter(file)) {
			out.write(generate());
		}
	}

	public void setContext(final Map<String, String> context) {
		properties.putAll(context);
	}

	public void add(final String key, final String value) {
		properties.put(key, value);
	}

	/**
	 * Append value to the value of a context key
	 *
	 * @param context
	 * @param key
	 * @param valueJavaDTOVisitor
	 * @return
	 */
	public static Map<String, String> append(final Map<String, String> context, final String key, final String value) {
		final String current = context.get(key);
		if (current != null) {
			context.put(key, current + value);
		} else {
			context.put(key, value);
		}
		return context;
	}
}
