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
import java.net.URISyntaxException;

import ch.skymarshall.annotations.GuiObject;
import ch.skymarshall.util.generators.Template;
import ch.skymarshall.util.helpers.ClassFinder;

public class GuiModelGenerator {

	public static void main(final String[] args) throws IOException, URISyntaxException {
		System.out.println("Running in " + new File(".").getAbsolutePath()); // NOSONAR
		final File target;
		if (args.length > 0) {
			target = new File(args[0]);
		} else {
			target = new File("src-generated");
		}
		target.mkdirs();
		new GuiModelGenerator().process(target);
	}

	private void process(final File targetSrcFolder) throws IOException, URISyntaxException {

		final ClassFinder finder = ClassFinder.forThread();
		finder.addExpectedAnnotation(GuiObject.class, ClassFinder.Policy.CLASS_ONLY);
		finder.collect();
		System.out.println(finder.getResult()); // NOSONAR

		for (final Class<?> clazz : finder.getResult()) {
			final String pkg = clazz.getPackage().getName();
			final File targetFolder = new File(targetSrcFolder, pkg.replace('.', '/'));

			final ModelClassProcessor processor = new ModelClassProcessor(clazz);
			final Template generatedClassTemplate = processor.process();
			generatedClassTemplate.add("package", pkg);
			generatedClassTemplate.writeTo(new File(targetFolder, processor.getClassName() + ".java"));
		}

	}

}
