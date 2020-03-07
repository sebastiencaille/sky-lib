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
import ch.skymarshall.util.helpers.ClassFinder;

public class GuiModelGenerator {

	public static void main(final String[] args) throws IOException {
		System.out.println("Running in " + new File(".").getAbsolutePath()); // NOSONAR
		final File target;
		final String sourcePackage = args[0];
		if (args.length > 1) {
			target = new File(args[1]);
		} else {
			target = new File("src-generated");
		}
		target.mkdirs();
		new GuiModelGenerator().process(sourcePackage, target);
	}

	private void process(final String sourcePackage, final File targetSrcFolder) throws IOException {

		final ClassFinder finder = ClassFinder.forApp();
		finder.addExpectedAnnotation(GuiObject.class, ClassFinder.Policy.CLASS_ONLY);
		finder.collect(sourcePackage);
		System.out.println(finder.getResult()); // NOSONAR

		for (final Class<?> clazz : finder.getResult()) {
			new ModelClassProcessor(clazz).process().writeToFolder(targetSrcFolder);
		}

	}

}
