package ch.skymarshall.dataflowmgr.generator.writers.dot;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.regex.Pattern;

import ch.skymarshall.dataflowmgr.generator.model.Module;
import ch.skymarshall.dataflowmgr.generator.writers.AbstractWriter;
import ch.skymarshall.dataflowmgr.generator.writers.dot.DotModuleVisitor.Graph;

public class DotFileWriter extends AbstractWriter {

	private final File outputFolder;

	public DotFileWriter(final File outputFolder) {
		this.outputFolder = outputFolder;
	}

	@Override
	public File getModuleLocation(final Module module) {
		return new File(outputFolder, config.modulePattern.replaceAll(Pattern.quote("${module.name}"), module.name));
	}

	public void generate() {
		for (final Module module : modules) {
			new DotModuleVisitor(module, this).visit(new Graph());
		}
	}

	public static void main(final String[] args) throws FileNotFoundException, IOException {
		final File configFile = new File(args[0]);
		final File outputFolder = new File(args[1]);
		outputFolder.mkdirs();

		final DotFileWriter writer = new DotFileWriter(outputFolder);
		writer.configure(configFile);

		for (int i = 2; i < args.length; i++) {
			writer.loadModule(new File(args[i]));
		}
		writer.loadTransformers();
		writer.loadTemplates();

		writer.generate();

	}
}
