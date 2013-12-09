/*******************************************************************************
 * Copyright (c) 2013 Sebastien Caille.
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms are permitted
 * provided that the above copyright notice and this paragraph are
 * duplicated in all such forms and that any documentation,
 * advertising materials, and other materials related to such
 * distribution and use acknowledge that the software was developed
 * by Sebastien Caille.  The name of Sebastien Caille may not be used to endorse or promote products derived
 * from this software without specific prior written permission.
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND WITHOUT ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 ******************************************************************************/
package org.skymarshall.hmi.mvc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URLClassLoader;

import org.skymarshall.ClassSelector;
import org.skymarshall.hmi.HmiObject;

public class HmiModelGenerator {

    public static void main(final String[] args) throws ClassNotFoundException, IOException, URISyntaxException {
        final File target;
        if (args.length > 0) {
            target = new File(args[0]);
        } else {
            target = new File("src-generated");
        }
        target.mkdirs();
        new HmiModelGenerator().process(target);
    }

    private void process(final File targetSrcFolder) throws ClassNotFoundException, IOException, URISyntaxException {
        final ClassSelector selector = new ClassSelector((URLClassLoader) HmiModelGenerator.class.getClassLoader());
        selector.addExpectedTag(HmiObject.class.getName(), ClassSelector.Policy.CLASS_ONLY);
        selector.collect();
        System.out.println(selector.getResult());

        for (final Class<?> clazz : selector.getResult()) {
            final String pkg = clazz.getPackage().getName();
            final File targetFolder = new File(targetSrcFolder, pkg.replace('.', '/'));
            final HmiClassProcessor processor = new HmiClassProcessor(clazz, pkg);
            processor.process();
            final File outputFile = new File(targetFolder, processor.getClassName() + ".java");
            System.out.println("Generating " + outputFile.getAbsolutePath());
            outputFile.getParentFile().mkdirs();
            final FileOutputStream fout = new FileOutputStream(outputFile);
            fout.write(processor.getOutput().getBytes());
            fout.close();
        }

    }
}
