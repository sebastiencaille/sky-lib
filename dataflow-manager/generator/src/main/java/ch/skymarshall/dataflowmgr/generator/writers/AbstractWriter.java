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

import static ch.skymarshall.util.helpers.ClassLoaderHelper.openResourceStream;
import static ch.skymarshall.util.helpers.ClassLoaderHelper.readUTF8Resource;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import ch.skymarshall.util.generators.Template;

import ch.skymarshall.dataflowmgr.generator.JsonAdapter;
import ch.skymarshall.dataflowmgr.generator.model.ActionPoint;
import ch.skymarshall.dataflowmgr.generator.model.Dto;
import ch.skymarshall.dataflowmgr.generator.model.Flow;
import ch.skymarshall.dataflowmgr.generator.model.Module;
import ch.skymarshall.dataflowmgr.generator.model.TemplateType;
import ch.skymarshall.dataflowmgr.generator.model.Transformer;

public abstract class AbstractWriter {

	protected final JsonAdapter jsonAdapter;
	protected Config config;
	protected Registry registry;
	protected final List<Module> modules = new ArrayList<>();
	private String commandLine;

	public AbstractWriter() {
		jsonAdapter = new JsonAdapter();
	}

	public void configure(final File configFile, final String commandLine) throws IOException {
		configure(() -> getFileStream(configFile), commandLine);
	}

	private InputStream getFileStream(final File configFile) {
		try {
			return new BufferedInputStream(new FileInputStream(configFile));
		} catch (final FileNotFoundException e) {
			throw new IllegalStateException("unable to load config file", e);
		}
	}

	public void configure(final Supplier<InputStream> streamSupplier, final String commandLine) throws IOException {
		this.commandLine = commandLine;
		try (InputStream configIn = streamSupplier.get()) {
			config = jsonAdapter.readConfig(configIn);
		}
		registry = new Registry();
		registry.addTransformer(new Transformer());

	}

	public void loadModule(final File file) throws IOException {
		loadModule(() -> getFileStream(file));
	}

	public void loadModule(final Supplier<InputStream> streamSupplier) throws IOException {

		try (InputStream in = streamSupplier.get()) {
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

	protected void loadTransformers() {
		registry.getActions().stream().map(a -> a.action.template).allMatch(t -> getOrLoadTransformer(t) != null);
	}

	private Transformer getOrLoadTransformer(final String transformerName) {
		Transformer template = registry.getTransformer(transformerName);
		if (template != null) {
			return template;
		}

		try (InputStream in = openResourceStream(templateResourcePath(transformerName + ".transformer"))) {
			template = jsonAdapter.readTransformer(transformerName, in);
			template.init();
			registry.addTransformer(template);
			return template;
		} catch (final IOException e) {
			throw new IllegalStateException("Cannot read transformer " + transformerName, e);
		}

	}

	protected void loadTemplates() throws IOException {
		for (final TemplateType template : TemplateType.values()) {
			final String resourceName = templateResourcePath(template.name() + ".template");
			final Template newTemplate = new Template(readUTF8Resource(resourceName));
			newTemplate.setCommandLine(commandLine);
			registry.addTemplate(template, newTemplate);
		}
	}

	private String templateResourcePath(final String name) {
		return "templates/" + config.language + "/" + name.toLowerCase();
	}

	public abstract File getModuleLocation(Module module);

	public String getCommandLine() {
		return commandLine;
	}

	public File getOutputFile(final Module module, final String flowname, final String ext) {
		return new File(getModuleLocation(module), flowname + "." + ext);
	}
}