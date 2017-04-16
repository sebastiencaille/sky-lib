package ch.skymarshall.dataflowmgr.generator.model;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.skymarshall.dataflowmgr.generator.exceptions.TemplateException;

public class Template {

	private static final Logger LOGGER = LoggerFactory.getLogger(Template.class);

	private final String content;
	private final Map<String, String> properties = new HashMap<>();

	public enum TEMPLATE {
		DTO, FIELD, ACTION, FLOW
	}

	public Template(final String content) {
		this.content = content;
	}

	@Override
	public Template clone() {
		return new Template(content);
	}

	public String generate() {
		LOGGER.info("Generating with properties " + properties);
		final StringBuilder result = new StringBuilder("// File generated from template\n");
		int nextVariable = 0;
		int pos = 0;
		while ((nextVariable = content.indexOf("${", nextVariable)) > 0) {
			if (nextVariable > 0 && content.charAt(nextVariable - 1) == '$') {
				// it's a $$, skip
				nextVariable++;
				continue;
			}
			final String variable = content.substring(nextVariable + 2, content.indexOf("}", nextVariable));
			final String value = properties.get(variable);
			if (value == null) {
				throw new TemplateException("No value for property " + variable);
			}
			LOGGER.debug("replacing " + variable + " with " + value);
			result.append(content.substring(pos, nextVariable)).append(value);
			nextVariable = nextVariable + variable.length() + 3;
			pos = nextVariable;
		}
		result.append(content.substring(pos, content.length()));
		return result.toString();
	}

	public void write(final File file) throws IOException {
		LOGGER.info("Writing " + file);
		file.getParentFile().mkdirs();
		try (FileWriter out = new FileWriter(file)) {
			out.write(generate());
		}
	}

	public void setContext(final Map<String, String> context) {
		properties.putAll(context);
	}

}
