package ch.skymarshall.dataflowmgr.generator.writers.dot;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

import ch.skymarshall.dataflowmgr.generator.model.Module;
import ch.skymarshall.dataflowmgr.generator.writers.AbstractWriter;
import ch.skymarshall.dataflowmgr.generator.writers.dot.DotModuleVisitor.Graph;
import ch.skymarshall.dataflowmgr.model.Step;

public class DotFileWriter extends AbstractWriter {

	private final File outputFolder;

	private Set<Step> steps = new HashSet<>();

	public DotFileWriter(final File outputFolder) {
		this.outputFolder = outputFolder;
	}

	@Override
	public File getModuleLocation(final Module module) {
		return new File(outputFolder, config.modulePattern.replaceAll(Pattern.quote("${module.name}"), module.name));
	}

	public void generate() {
		for (final Module module : modules) {
			final Graph context = new Graph();
			for (final Step step : steps) {
				context.executed.add(step.uuid);
			}
			new DotModuleVisitor(module, this).visit(context);
		}
	}

	public static void main(final String[] args) throws FileNotFoundException, IOException {
		final File configFile = new File(args[0]);
		final File outputFolder = new File(args[1]);
		final File report = new File(args[2]);
		outputFolder.mkdirs();

		final DotFileWriter writer = new DotFileWriter(outputFolder);
		writer.configure(configFile);

		if (report.exists()) {
			writer.loadReport(report);
		}

		for (int i = 3; i < args.length; i++) {
			writer.loadModule(new File(args[i]));
		}
		writer.loadTransformers();
		writer.loadTemplates();

		writer.generate();

	}

	private void loadReport(final File report) throws JsonParseException, JsonMappingException, IOException {
		final ObjectMapper mapper = new ObjectMapper();
		final byte[] json = Files.readAllBytes(report.toPath());
		steps = mapper.readValue(json,
				TypeFactory.defaultInstance().constructCollectionLikeType(HashSet.class, Step.class));

	}
}
