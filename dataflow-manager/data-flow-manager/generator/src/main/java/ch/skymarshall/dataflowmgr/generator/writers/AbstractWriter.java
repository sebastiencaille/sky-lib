package ch.skymarshall.dataflowmgr.generator.writers;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import ch.skymarshall.dataflowmgr.generator.JsonAdapter;
import ch.skymarshall.dataflowmgr.generator.Utils;
import ch.skymarshall.dataflowmgr.generator.exceptions.TransformerException;
import ch.skymarshall.dataflowmgr.generator.model.ActionPoint;
import ch.skymarshall.dataflowmgr.generator.model.Dto;
import ch.skymarshall.dataflowmgr.generator.model.Flow;
import ch.skymarshall.dataflowmgr.generator.model.Module;
import ch.skymarshall.dataflowmgr.generator.model.Template;
import ch.skymarshall.dataflowmgr.generator.model.Template.TEMPLATE;
import ch.skymarshall.dataflowmgr.generator.model.Transformer;
import ch.skymarshall.dataflowmgr.generator.writers.java.JavaModuleVisitor;

public abstract class AbstractWriter {

	protected final JsonAdapter jsonAdapter;
	protected Config config;
	protected Registry registry;
	private final List<Module> modules = new ArrayList<>();

	public AbstractWriter() {
		jsonAdapter = new JsonAdapter();
	}

	public void configure(final File configFile)
			throws JsonParseException, JsonMappingException, IOException, FileNotFoundException {
		try (FileInputStream configIn = new FileInputStream(configFile)) {
			config = jsonAdapter.readConfig(configIn);
		}
		registry = new Registry();
		final Transformer idTemplate = new Transformer();
		registry.addTransformer(idTemplate);
	}

	protected void loadModule(final File file) throws FileNotFoundException, IOException {

		try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(file))) {
			final Module module = jsonAdapter.readApplication(in);
			for (final Dto dto : module.dtos) {
				registry.addDto(dto);
			}
			for (final ActionPoint action : module.actions) {
				registry.addActionPoint(action);
			}
			for (final Flow flow : module.flows) {
				registry.addFlow(flow);
			}
			modules.add(module);
		}

	}

	public void generate() {
		for (final Module module : modules) {
			new JavaModuleVisitor(module, this).visit(new HashMap<>());
		}
	}

	private InputStream openResourceStream(final String filename) {
		final ClassLoader cl = Thread.currentThread().getContextClassLoader();
		final String resourceName = "templates/" + config.language + "/" + filename;
		final InputStream in = cl.getResourceAsStream(resourceName);
		if (in == null) {
			throw new IllegalArgumentException("No such file: " + resourceName);
		}
		return in;
	}

	private Transformer getOrLoadTransformer(final String transformerName) throws TransformerException {
		Transformer template = registry.getTransformer(transformerName);
		if (template != null) {
			return template;
		}

		try (InputStream in = openResourceStream(transformerName + ".json")) {
			template = jsonAdapter.readTransformer(transformerName, in);
			template.init();
			registry.addTransformer(template);
			return template;
		} catch (final IOException e) {
			throw new IllegalStateException("Cannot read transformer " + transformerName, e);
		}

	}

	protected void loadTransformers() throws TransformerException {

		registry.getActions().stream().map((a) -> a.action.template).allMatch((t) -> getOrLoadTransformer(t) != null);
	}

	protected void loadTemplates() throws IOException {
		for (final TEMPLATE template : TEMPLATE.values()) {
			try (InputStream in = openResourceStream(template.name().toLowerCase() + ".template")) {
				registry.addTemplate(template, new Template(Utils.read(in)));
			}
		}
	}

	public abstract File getModuleLocation(Module module);
}
