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
package ch.scaille.gui.mvc;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import ch.scaille.annotations.GuiObject;
import ch.scaille.generators.util.CodeGeneratorParams;
import ch.scaille.util.helpers.ClassFinder;
import ch.scaille.util.helpers.Log;

public class GuiModelGenerator {

	public static void main(final String[] args) throws IOException {
		final CodeGeneratorParams params = CodeGeneratorParams.parse(args);
		new GuiModelGenerator().process(params);
	}

	private void process(final CodeGeneratorParams params) throws IOException {
		final Path targetFolder = Paths.get(params.getTargetFolder());
		Files.createDirectories(targetFolder);

		Log.of(this).info("Scanning " + params.getSourceFolder());
		final ClassFinder finder = ClassFinder.source(new File(params.getSourceFolder()))
				.withAnnotation(GuiObject.class, ClassFinder.Policy.CLASS_ONLY).collect(params.getNamespaceFilter());
		Log.of(this).info("Processing classes:" + finder.getResult());

		for (final Class<?> clazz : finder.getResult()) {
			new ModelClassProcessor(clazz).process().writeToFolder(targetFolder);
		}

	}

}
