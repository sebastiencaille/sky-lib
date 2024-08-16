package ch.scaille.generators.util;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import ch.scaille.util.helpers.ClassLoaderHelper;
import ch.scaille.util.helpers.Logs;

/**
 * Simple template made of a content with placeholders (${...}) and properties
 * (placeholder as key + value)
 * <p>
 * 	 The {@link GenerationMetadata} allow to pass information about the generation, and can be used the following way:<br>
 *   java: @Generated(value = "${generator}", date = "${generationDate}", comments="${commandLine}")
 * </p>
 * @author scaille
 *
 */
public class Template {

	private final String content;
	private final Map<String, String> properties = new HashMap<>();

	private String preferredFile;

	public static Template from(final File file) throws IOException {
		return new Template(Files.readString(file.toPath()));
	}

	public static Template from(final String resource) throws IOException {
		return new Template(ClassLoaderHelper.readUTF8Resource(resource));
	}

	public Template(final String content) {
		this.content = content.replace("\r", "");
	}

	public String getPreferedFileName() {
		return preferredFile;
	}

	public Template apply(final Map<String, String> templateProperties, final String providedPreferredFile,
			GenerationMetadata generationMetadata) {
		final var newTemplate = instantiate(content);
		newTemplate.preferredFile = providedPreferredFile;
		newTemplate.setContext(templateProperties);
		if (generationMetadata != null) {
			newTemplate.withGenerationMetadata(generationMetadata);
		}
		return newTemplate;
	}

	protected Template instantiate(final String newContent) {
		return new Template(newContent);
	}

	public Template withGenerationMetadata(GenerationMetadata generationMetadata) {
		this.properties.put("generator", generationMetadata.getGenerator());
		this.properties.put("commandLine", generationMetadata.getComments());
		this.properties.put("generationDate", generationMetadata.getGenerationDate());
		return this;
	}

	public String generate() {
		final var result = new StringBuilder(1000);
		int nextVariablePos = 0;
		int currentPos = 0;
		while ((nextVariablePos = content.indexOf("${", nextVariablePos)) > 0) {
			if (content.charAt(nextVariablePos - 1) == '$') {
				// it's a $$, skip
				nextVariablePos++;
				continue;
			}

			final var variable = content.substring(nextVariablePos + 2, content.indexOf('}', nextVariablePos));
			final var value = properties.get(variable);
			if (value == null) {
				throw new TemplateException("No value for property " + variable);
			}
			final var indent = getIndentation(nextVariablePos);
			result.append(content, currentPos, nextVariablePos).append(value.replace("\n", indent));
			nextVariablePos = nextVariablePos + variable.length() + 3;
			currentPos = nextVariablePos;
		}
		result.append(content.substring(currentPos));
		return result.toString();
	}

	private String getIndentation(final int nextVariablePos) {
		var prevEol = nextVariablePos;
		var startOfText = nextVariablePos;
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
	 * @return the path of the written file
	 */
	public Path writeToFolder(final Path folder) throws IOException {
		if (preferredFile == null) {
			throw new IllegalStateException("preferredFile is not set");
		}
		return writeTo(folder.resolve(preferredFile));
	}

	/**
	 * Writes the resulting content in a file
	 */
	public void writeTo(final Consumer<String> writer) {
		writer.accept(generate());
	}

	/**
	 * Writes the resulting content in a file
	 */
	public <U> U writeTo(final Function<String, U> writer) {
		return writer.apply(generate());
	}

	/**
	 * Writes the resulting content in the preferred file
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
     */
	public void setContext(final Map<String, String> context) {
		properties.putAll(context);
	}

	/**
	 * Adds a value to the current context
	 */
	public void add(final String key, final String value) {
		properties.put(key, value);
	}

	/**
	 * Appends a value to the value of a context key
	 */
	public static Map<String, String> append(final Map<String, String> context, final String key, final String value) {
		context.put(key, context.getOrDefault(key, "") + value);
		return context;
	}

	/**
	 * Appends a value to the value of a context key, adding a comma if needed
	 */
	public static Map<String, String> appendToList(final Map<String, String> context, final String key,
			final String value) {
		var existing = context.getOrDefault(key, "");
		if (!existing.isEmpty()) {
			existing = existing + ", ";
		}
		existing += value;
		context.put(key, existing);
		return context;
	}
}
