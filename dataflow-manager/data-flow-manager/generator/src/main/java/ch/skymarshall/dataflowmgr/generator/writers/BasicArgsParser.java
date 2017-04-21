package ch.skymarshall.dataflowmgr.generator.writers;

import static java.util.Arrays.asList;

import java.io.File;
import java.util.List;

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

}