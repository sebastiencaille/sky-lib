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
package ch.skymarshall.dataflowmgr.generator.writers.dot;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

import ch.skymarshall.dataflowmgr.generator.model.Module;
import ch.skymarshall.dataflowmgr.generator.writers.AbstractWriter;
import ch.skymarshall.dataflowmgr.generator.writers.BasicArgsParser;
import ch.skymarshall.dataflowmgr.generator.writers.dot.DotModuleVisitor.Graph;
import ch.skymarshall.dataflowmgr.model.Step;
import joptsimple.ArgumentAcceptingOptionSpec;

public class DotFileWriter extends AbstractWriter {

	private final File outputFolder;

	private Set<Step> steps = new HashSet<>();

	private Set<Step> expectedSteps = new HashSet<>();

	private final ObjectMapper mapper = new ObjectMapper();

	public DotFileWriter(final File outputFolder) {
		this.outputFolder = outputFolder;
	}

	@Override
	public File getModuleLocation(final Module module) {
		return new File(outputFolder, config.modulePattern.replaceAll(Pattern.quote("${module.name}"), module.name));
	}

	private void loadStepsReport(final File report) throws IOException {
		final byte[] json = Files.readAllBytes(report.toPath());
		this.steps = mapper.readValue(json,
				TypeFactory.defaultInstance().constructCollectionLikeType(HashSet.class, Step.class));
	}

	private void loadExpectedSteps(final File expectedSteps) throws IOException {
		final byte[] json = Files.readAllBytes(expectedSteps.toPath());
		this.expectedSteps = mapper.readValue(json,
				TypeFactory.defaultInstance().constructCollectionLikeType(HashSet.class, Step.class));
	}

	public void generate() {
		for (final Module module : modules) {
			final Graph context = new Graph();
			for (final Step step : steps) {
				context.executed.add(step.uuid);
			}
			for (final Step step : expectedSteps) {
				context.expected.add(step.uuid);
			}
			new DotModuleVisitor(module, this).visit(context);
		}
	}

	public static void main(final String[] args) throws IOException {

		final BasicArgsParser argsParser = new BasicArgsParser();
		final ArgumentAcceptingOptionSpec<File> stepsReportOpt = argsParser.getParser().accepts("r", "steps report")
				.withRequiredArg().ofType(File.class);
		final ArgumentAcceptingOptionSpec<File> expectedStepsOpt = argsParser.getParser().accepts("e", "expected steps")
				.availableIf(stepsReportOpt).withRequiredArg().ofType(File.class);
		argsParser.parse(args);

		final File configFile = argsParser.getConfigFile();
		final File outputFolder = argsParser.getOutputFolder();
		final File stepsReport = argsParser.getOptions().valueOf(stepsReportOpt);
		final File expectedSteps = argsParser.getOptions().valueOf(expectedStepsOpt);
		outputFolder.mkdirs();

		final DotFileWriter writer = new DotFileWriter(outputFolder);
		writer.configure(configFile, BasicArgsParser.line(DotFileWriter.class, args));

		if (stepsReport != null) {
			writer.loadStepsReport(stepsReport);
		}

		if (expectedSteps != null) {
			writer.loadExpectedSteps(expectedSteps);
		}

		for (final File flow : argsParser.getFlows()) {
			writer.loadModule(flow);
		}
		writer.loadTransformers();
		writer.loadTemplates();

		writer.generate();

	}

}
