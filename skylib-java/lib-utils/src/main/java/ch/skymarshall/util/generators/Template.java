package ch.skymarshall.util.generators;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import ch.skymarshall.util.helpers.ClassLoaderHelper;

/**
 * Simple template made of a content with place holders (${...}) and properties
 * (place holder as key + value)
 *
 * @author scaille
 *
 */
public class Template {

	private final String content;
	private final Map<String, String> properties = new HashMap<>();

	private String commandLine;
	private String preferedFile;

	public static final Template from(final File file) throws IOException {
		return new Template(new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8));
	}

	public static final Template from(final String resource) throws IOException {
		return new Template(ClassLoaderHelper.readUTF8Resource(resource));
	}

	public Template(final String content) {
		this.content = content.replace("\r", "");
	}

	public String getPreferedFileName() {
		return preferedFile;
	}

	public Template apply(final Map<String, String> templateProperties, final String providedPreferedFile) {
		final Template newTemplate = instantiate(content);
		newTemplate.preferedFile = providedPreferedFile;
		newTemplate.setCommandLine(commandLine);
		newTemplate.setContext(templateProperties);
		return newTemplate;
	}

	protected Template instantiate(final String newContent) {
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
			result.append(content.substring(currentPos, nextVariablePos)).append(value.replace("\n", indent));
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

	public File writeTo(final File file) throws IOException {
		Logger.getLogger(Template.class.getName()).info(() -> "Writing " + file);
		file.getParentFile().mkdirs();

		try (final FileWriter out = new FileWriter(file)) {
			out.write(generate());
		}
		return file;
	}

	public File writeToFolder(final File folder) throws IOException {
		if (preferedFile == null) {
			throw new IllegalStateException("preferedFile is not set");
		}
		return writeTo(new File(folder, preferedFile));
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
		context.put(key, context.getOrDefault(key, "") + value);
		return context;
	}
	
	/**
	 * Append value to the value of a context key, adding a comma if needed
	 *
	 * @param context
	 * @param key
	 * @param valueJavaDTOVisitor
	 * @return
	 */
	public static Map<String, String> appendToList(final Map<String, String> context, final String key, final String value) {
		String existing = context.getOrDefault(key, "");
		if (!existing.isEmpty()) {
			existing = existing + ", ";
		}
		existing += value;
		context.put(key,  existing);
		return context;
	}
}
