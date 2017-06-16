/*******************************************************************************
 * Copyright (c) 2017 Sebastien Caille.
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms are permitted
 *  provided that the above copyright notice and this paragraph are
 *  duplicated in all such forms and that any documentation,
 *  advertising materials, and other materials related to such
 *  distribution and use acknowledge that the software was developed
 *  by Sebastien Caille.  The name of Sebastien Caille may not be used to endorse or promote products derived
 *  from this software without specific prior written permission.
 *  THIS SOFTWARE IS PROVIDED ``AS IS'' AND WITHOUT ANY EXPRESS OR
 *  IMPLIED WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 ******************************************************************************/
package ch.skymarshall.dataflowmgr.generator.writers.singlenode;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.regex.Pattern;

import ch.skymarshall.dataflowmgr.generator.model.Module;
import ch.skymarshall.dataflowmgr.generator.writers.AbstractWriter;
import ch.skymarshall.dataflowmgr.generator.writers.BasicArgsParser;

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
			new SingleNodeJavaModuleGenerator(module, this).visit(new HashMap<>());
		}
	}

	public static void main(final String[] args) throws FileNotFoundException, IOException {

		final BasicArgsParser argsParser = new BasicArgsParser().parse(args);

		final SingleNodeWriter writer = new SingleNodeWriter(argsParser.getOutputFolder());
		writer.configure(argsParser.getConfigFile(), BasicArgsParser.line(SingleNodeWriter.class, args));

		for (final File file : argsParser.getFlows()) {
			writer.loadModule(file);
		}
		writer.loadTransformers();
		writer.loadTemplates();

		argsParser.getOutputFolder().mkdirs();
		writer.generate();

	}

}
