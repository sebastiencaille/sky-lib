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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import ch.skymarshall.dataflowmgr.generator.exceptions.TransformerException;

public class Transformer {

	@JsonIgnore
	public String name = "";

	@JsonProperty(defaultValue = "")
	public String regexp;

	@JsonProperty(defaultValue = "")
	public String[] variables;

	@JsonProperty(defaultValue = "")
	public String output;

	@JsonProperty(defaultValue = "false")
	public boolean identity;

	@JsonProperty(defaultValue = "")
	public String replace;

	@JsonProperty(defaultValue = "")
	public String replaceWith;

	@JsonIgnore
	private Pattern compile;

	public void init() throws TransformerException {
		if (!regexp.isEmpty()) {
			if (variables == null) {
				throw new TransformerException("regexp is set -> variables must not be empty");
			}
			if (output == null) {
				throw new TransformerException("regexp is set -> output must not be empty");
			}
			compile = Pattern.compile(regexp);
		} else if (!replace.isEmpty() && replaceWith.isEmpty()) {
			throw new TransformerException("replace is set -> replaceWith must not be empty");
		} else {
			throw new TransformerException("No transformer specified");
		}
	}

	public String transform(final String in) throws TransformerException {
		if (identity) {
			return in;
		} else if (!regexp.isEmpty()) {
			final Matcher matcher = compile.matcher(in);
			if (!matcher.matches()) {
				throw new IllegalStateException("Template " + name + ": cannot match " + in);
			}
			if (matcher.groupCount() != variables.length) {
				throw new IllegalStateException("Template " + name + ": " + matcher.groupCount() + " groups, "
						+ variables.length + " variables");
			}
			String copy = output;
			for (int i = 0; i < variables.length; i++) {
				copy = copy.replaceAll("\\$" + variables[i], matcher.group(i + 1));
			}
			copy = copy.replaceAll("\\$\\$", "$");
			return copy;
		} else if (!replace.isEmpty()) {

			return in.replaceAll(replace, replaceWith);
		} else {
			throw new TransformerException("No transformer specified");
		}
	}

}
