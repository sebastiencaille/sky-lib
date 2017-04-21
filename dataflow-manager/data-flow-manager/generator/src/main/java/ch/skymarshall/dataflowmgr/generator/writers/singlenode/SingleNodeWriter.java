package ch.skymarshall.dataflowmgr.generator.writers.singlenode;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.regex.Pattern;

import ch.skymarshall.dataflowmgr.generator.model.Module;
import ch.skymarshall.dataflowmgr.generator.writers.AbstractWriter;
import ch.skymarshall.dataflowmgr.generator.writers.BasicArgsParser;
import ch.skymarshall.dataflowmgr.generator.writers.java.JavaModuleVisitor;

public class SingleNodeWriter extends AbstractWriter {

	private final File outputFolder;

	public SingleNodeWriter(final File outputFolder) {
		this.outputFolder = outputFolder;
	}

	@Override
	public File getModuleLocation(final Module module) {
		return new File(outputFolder, config.modulePattern.replaceAll(Pattern.quote("${module.name}"), module.name));
	}

	public void generate() {
		for (final Module module : modules) {
			new JavaModuleVisitor(module, this).visit(new HashMap<>());
		}
	}

	public static void main(final String[] args) throws FileNotFoundException, IOException {

		final BasicArgsParser argsParser = new BasicArgsParser().parse(args);

		final SingleNodeWriter writer = new SingleNodeWriter(argsParser.getOutputFolder());
		writer.configure(argsParser.getConfigFile());

		for (final File file : argsParser.getFlows()) {
			writer.loadModule(file);
		}
		writer.loadTransformers();
		writer.loadTemplates();

		argsParser.getOutputFolder().mkdirs();
		writer.generate();

	}

}
