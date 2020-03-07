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

public class DotFileWriter {
}
//extends AbstractWriter {
//
//	private final File outputFolder;
//
//	private Set<Step> steps = new HashSet<>();
//
//	private Set<Step> expectedSteps = new HashSet<>();
//
//	private final ObjectMapper mapper = new ObjectMapper();
//
//	private String outputName;
//
//	public DotFileWriter(final File outputFolder) {
//		this.outputFolder = outputFolder;
//	}
//
//	@Override
//	public File getModuleLocation(final Module module) {
//		return new File(outputFolder, config.modulePattern.replaceAll(Pattern.quote("${module.name}"), module.name));
//	}
//
//	public void loadStepsReport(final File report) throws IOException {
//		final byte[] json = Files.readAllBytes(report.toPath());
//		this.steps = mapper.readValue(json,
//				TypeFactory.defaultInstance().constructCollectionLikeType(HashSet.class, Step.class));
//	}
//
//	public void loadExpectedSteps(final File expectedStepsFile) throws IOException {
//		final byte[] json = Files.readAllBytes(expectedStepsFile.toPath());
//		this.expectedSteps = mapper.readValue(json,
//				TypeFactory.defaultInstance().constructCollectionLikeType(HashSet.class, Step.class));
//	}
//
//	private void setOutputName(final String outputName) {
//		this.outputName = outputName;
//	}
//
//	@Override
//	public File getOutputFile(final Module module, final String flowname, final String ext) {
//		if (outputName != null) {
//			return super.getOutputFile(module, outputName, ext);
//		}
//		return super.getOutputFile(module, flowname, ext);
//	}
//
//	public void generate() {
//		for (final Module module : modules) {
//			final Graph context = new Graph();
//			for (final Step step : steps) {
//				context.executed.add(step.uuid);
//			}
//			for (final Step step : expectedSteps) {
//				context.expected.add(step.uuid);
//			}
//			new DotModuleVisitor(module, this).visit(context);
//		}
//	}
//
//	public void toPng(final String flowName, final String postfix) throws IOException, InterruptedException {
//		final Module module = modules.stream().filter(m -> m.flows.stream().anyMatch(f -> flowName.equals(f.name)))
//				.findFirst().orElseThrow(() -> new IllegalArgumentException("flowName not found in flows"));
//		final String[] cmdarray = new String[] { "dot", "-Tpng",
//				"-o" + getOutputFile(module, flowName + "-" + postfix, "png").toString(),
//				getOutputFile(module, flowName, "dot").toString() };
//		final Process exec = Runtime.getRuntime().exec(cmdarray);
//		int len;
//		final byte[] buffer = new byte[1024];
//		while ((len = exec.getErrorStream().read(buffer)) > 0) {
//			System.err.write(buffer, 0, len);
//		}
//		if (exec.waitFor() != 0) {
//			throw new IllegalStateException("Png generation failed");
//		}
//
//	}
//
//	public static void main(final String[] args) throws IOException {
//
//		final BasicArgsParser argsParser = new BasicArgsParser();
//		final ArgumentAcceptingOptionSpec<File> stepsReportOpt = argsParser.getParser().accepts("r", "steps report")
//				.withRequiredArg().ofType(File.class);
//		final ArgumentAcceptingOptionSpec<File> expectedStepsOpt = argsParser.getParser()
//				.accepts("e", "expected steps report").availableIf(stepsReportOpt).withRequiredArg().ofType(File.class);
//		final ArgumentAcceptingOptionSpec<String> outputOpt = argsParser.getParser()
//				.accepts("o", "output filename (no extension)").withRequiredArg().ofType(String.class);
//		argsParser.parse(args);
//
//		final File configFile = argsParser.getConfigFile();
//		final File outputFolder = argsParser.getOutputFolder();
//		final File stepsReport = argsParser.getOptions().valueOf(stepsReportOpt);
//		final String outputFile = argsParser.getOptions().valueOf(outputOpt);
//		final File expectedSteps = argsParser.getOptions().valueOf(expectedStepsOpt);
//		outputFolder.mkdirs();
//
//		final DotFileWriter writer = new DotFileWriter(outputFolder);
//		writer.configure(configFile, BasicArgsParser.line(DotFileWriter.class, args));
//
//		if (stepsReport != null) {
//			writer.loadStepsReport(stepsReport);
//		}
//
//		if (expectedSteps != null) {
//			writer.loadExpectedSteps(expectedSteps);
//		}
//
//		if (outputFile != null) {
//			writer.setOutputName(outputFile);
//		}
//
//		for (final File flow : argsParser.getFlows()) {
//			writer.loadModule(flow);
//		}
//		writer.loadTransformers();
//		writer.loadTemplates();
//
//		writer.generate();
//
//	}
