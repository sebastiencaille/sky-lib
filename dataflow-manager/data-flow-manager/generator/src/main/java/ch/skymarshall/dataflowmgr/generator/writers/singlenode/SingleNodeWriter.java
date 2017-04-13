package ch.skymarshall.dataflowmgr.generator.writers.singlenode;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.regex.Pattern;

import ch.skymarshall.dataflowmgr.generator.model.Module;
import ch.skymarshall.dataflowmgr.generator.writers.AbstractWriter;

public class SingleNodeWriter extends AbstractWriter {

	private final File outputFolder;

	public SingleNodeWriter(final File outputFolder) {
		this.outputFolder = outputFolder;
	}

	@Override
	public File getModuleLocation(final Module module) {
		return new File(outputFolder, config.modulePattern.replaceAll(Pattern.quote("${module.name}"), module.name));
	}

	public static void main(final String[] args) throws FileNotFoundException, IOException {
		final File configFile = new File(args[0]);
		final File outputFolder = new File(args[1]);
		outputFolder.mkdirs();

		final SingleNodeWriter writer = new SingleNodeWriter(outputFolder);
		writer.configure(configFile);

		for (int i = 2; i < args.length; i++) {
			writer.loadModule(new File(args[i]));
		}
		writer.loadTransformers();
		writer.loadTemplates();

		writer.generate();

	}

}
