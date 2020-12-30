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
package ch.skymarshall.gui.mvc;

import java.io.File;
import java.io.IOException;

import ch.skymarshall.annotations.GuiObject;
import ch.skymarshall.generators.util.CodeGeneratorParams;
import ch.skymarshall.util.helpers.ClassFinder;
import ch.skymarshall.util.helpers.Log;

public class GuiModelGenerator {

	public static void main(final String[] args) throws IOException {
		final CodeGeneratorParams params = CodeGeneratorParams.parse(args);
		new GuiModelGenerator().process(params);
	}

	private void process(final CodeGeneratorParams params) throws IOException {
		final File targetFolder = new File(params.getTargetFolder());
		targetFolder.mkdirs();

		final ClassFinder finder = ClassFinder.source(new File(params.getSourceFolder()));
		finder.addExpectedAnnotation(GuiObject.class, ClassFinder.Policy.CLASS_ONLY);
		finder.collect(params.getNamespaceFilter());
		Log.of(this).info("Processing classes:"+ finder.getResult()); 

		for (final Class<?> clazz : finder.getResult()) {
			new ModelClassProcessor(clazz).process().writeToFolder(targetFolder);
		}

	}

}
