package ch.scaille.generators.util;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

import ch.scaille.util.helpers.ClassLoaderHelper;
import ch.scaille.util.helpers.Logs;

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
	private String preferredFile;

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
		return preferredFile;
	}

	public Template apply(final Map<String, String> templateProperties, final String providedPreferedFile) {
		final Template newTemplate = instantiate(content);
		newTemplate.preferredFile = providedPreferedFile;
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
		final StringBuilder result = new StringBuilder("// File generated from template ")
				.append(new SimpleDateFormat("yyyy/MM/dd hh:mm:ss").format(new Date())).append("\n");
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

	/**
	 * Writes the resulting content in a file
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public Path writeTo(final Path path) throws IOException {
		Logs.of(this).info(() -> "Writing " + path);
		Files.createDirectories(path.getParent());
		Files.write(path, generate().getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE,
				StandardOpenOption.TRUNCATE_EXISTING);
		return path;
	}

	/**
	 * Writes the resulting content in the preferred file
	 * 
	 * @param folder
	 * @return
	 * @throws IOException
	 */
	public Path writeToFolder(final Path folder) throws IOException {
		if (preferredFile == null) {
			throw new IllegalStateException("preferredFile is not set");
		}
		return writeTo(folder.resolve(preferredFile));
	}

	/**
	 * Writes the resulting content in a file
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public <U> U writeTo(final Function<String, U> writer) {
		return writer.apply(generate());
	}

	/**
	 * Writes the resulting content in the preferred file
	 * 
	 * @param folder
	 * @return
	 * @throws IOException
	 */
	public <U> U writeTo(final BiFunction<String, String, U> writer) {
		if (preferredFile == null) {
			throw new IllegalStateException("preferredFile is not set");
		}
		return writer.apply(preferredFile, generate());
	}

	/**
	 * Sets the current context
	 * 
	 * @param context
	 */
	public void setContext(final Map<String, String> context) {
		properties.putAll(context);
	}

	/**
	 * Adds a value to the current context
	 * 
	 * @param key
	 * @param value
	 */
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
	public static Map<String, String> appendToList(final Map<String, String> context, final String key,
			final String value) {
		String existing = context.getOrDefault(key, "");
		if (!existing.isEmpty()) {
			existing = existing + ", ";
		}
		existing += value;
		context.put(key, existing);
		return context;
	}
}
