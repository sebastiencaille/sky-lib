/*******************************************************************************
 * Copyright (c) 2017 Sebastien Caille.
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms are permitted
 *  provided that the above copyright notice and this paragraph are
 *  duplicated in all such forms and that any documentation,
 *  advertising materials, and other materials related to such
 *  distribution and use acknowledge that the software was developed
 *  by Sebastien Caille.  The name of Sebastien Caille may not be used to endorse or promote products derived
 *  from this software without specific prior written permission.
 *  THIS SOFTWARE IS PROVIDED ``AS IS'' AND WITHOUT ANY EXPRESS OR
 *  IMPLIED WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 ******************************************************************************/
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

	private String commandLine;

	public enum TEMPLATE {
		DTO, FIELD, ACTION, FLOW
	}

	public Template(final String content) {
		this.content = content;
	}

	@Override
	public Template clone() {
		final Template newTemplate = new Template(content);
		newTemplate.setCommandLine(commandLine);
		return newTemplate;
	}

	public void setCommandLine(final String commandLine) {
		this.commandLine = commandLine;
	}

	public String generate() {
		LOGGER.info("Generating with properties " + properties);
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
