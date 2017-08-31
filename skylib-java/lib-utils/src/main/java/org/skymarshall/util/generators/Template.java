package org.skymarshall.util.generators;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Template {

	private final String content;
	private final Map<String, String> properties = new HashMap<String, String>();

	private String commandLine;

	public Template(final String content) {
		this.content = content;
	}

	public Template apply(final Map<String, String> properties) {
		final Template newTemplate = create(content);
		newTemplate.setCommandLine(commandLine);
		newTemplate.setContext(properties);
		return newTemplate;
	}

	protected Template create(final String content) {
		return new Template(content);
	}

	public void setCommandLine(final String commandLine) {
		this.commandLine = commandLine;
	}

	public String generate() {
		final StringBuilder result = new StringBuilder("// File generated from template").append("\n");
		if (commandLine != null) {
			result.append("// ").append(commandLine).append("\n");
		}
		int nextVariable = 0;
		int pos = 0;
		while ((nextVariable = content.indexOf("${", nextVariable)) > 0) {
			if (nextVariable > 0 && content.charAt(nextVariable - 1) == '$') {
				// it's a $$, skip
				nextVariable++;
				continue;
			}
			final String variable = content.substring(nextVariable + 2, content.indexOf('}', nextVariable));
			final String value = properties.get(variable);
			if (value == null) {
				throw new TemplateException("No value for property " + variable);
			}
			result.append(content.substring(pos, nextVariable)).append(value);
			nextVariable = nextVariable + variable.length() + 3;
			pos = nextVariable;
		}
		result.append(content.substring(pos, content.length()));
		return result.toString();
	}

	public void write(final File file) throws IOException {
		file.getParentFile().mkdirs();
		final FileWriter out = new FileWriter(file);
		try {
			out.write(generate());
		} finally {
			out.close();
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
