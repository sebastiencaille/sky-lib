/*******************************************************************************
 * Copyright (c) 2017 Sebastien Caille.
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms are permitted
 *  provided that the above Copyrightnotice and this paragraph are
 *  duplicated in all such forms and that any documentation,
 *  advertising materials, and other materials related to such
 *  distribution and use acknowledge that the software was developed
 *  by Sebastien Caille.  The name of Sebastien Caille may not be used to endorse or promote products derived
 *  from this software without specific prior written permission.
 *  THIS SOFTWARE IS PROVIDED ``AS IS'' AND WITHOUT ANY EXPRESS OR
 *  IMPLIED WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 ******************************************************************************/
package ch.skymarshall.dataflowmgr.generator.writers;

import static java.util.Arrays.asList;

import java.io.File;
import java.util.List;
import java.util.stream.Stream;

import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

public class BasicArgsParser {

	private final ArgumentAcceptingOptionSpec<File> configOpt;
	private final ArgumentAcceptingOptionSpec<File> outputOpt;
	private final ArgumentAcceptingOptionSpec<File> flowsOpt;
	private final OptionParser parser;
	private OptionSet options;

	public BasicArgsParser() {
		parser = new OptionParser();
		configOpt = parser.accepts("c", "config").withRequiredArg().ofType(File.class).required();
		outputOpt = parser.accepts("o", "output folder").withRequiredArg().ofType(File.class).required();
		flowsOpt = parser.accepts("f", "flows").withRequiredArg().ofType(File.class).required()
				.withValuesSeparatedBy(':');
		parser.acceptsAll(asList("?", "h"), "help").isForHelp();
	}

	public OptionParser getParser() {
		return parser;
	}

	public BasicArgsParser parse(final String[] args) {
		options = parser.parse(args);
		return this;
	}

	public OptionSet getOptions() {
		return options;
	}

	public File getConfigFile() {
		return options.valueOf(configOpt);
	}

	public File getOutputFolder() {
		return options.valueOf(outputOpt);
	}

	public List<File> getFlows() {
		return options.valuesOf(flowsOpt);
	}

	public static String line(final Class<?> class1, final String[] args) {
		final StringBuilder builder = new StringBuilder(class1.getSimpleName());
		Stream.of(args).forEach(s -> builder.append(" ").append(s));
		return builder.toString();
	}

}
