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
package org.skymarshall;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ClassSelector {

	public enum Policy {
		ALL_SUBCLASSES, CLASS_ONLY
	}

	private static final String CLASS_EXTENSION = ".class";

	private static final Set<String> JAR_EXTENSIONS = new HashSet<>();
	static {
		JAR_EXTENSIONS.add(".jar");
		JAR_EXTENSIONS.add(".sar");
	}

	private final Map<Class<?>, Policy> result = new HashMap<>();

	private final Map<Class<?>, Policy> expectedTags = new HashMap<>();

	private final Set<Class<?>> expectedSuperClasses = new HashSet<>();

	private final URLClassLoader loader;

	public ClassSelector(final URLClassLoader loader) {
		this.loader = loader;
		result.put(Object.class, null);
	}

	public void addExpectedTag(final String tagClassName, final Policy policy) throws ClassNotFoundException {
		final Class<?> tag = Class.forName(tagClassName, true, loader);
		if (!tag.isAnnotation()) {
			throw new IllegalArgumentException("Class is not an annotation: " + tagClassName);
		}
		expectedTags.put(tag, policy);
	}

	public void addExpectedSuperClass(final String className) throws ClassNotFoundException {
		expectedSuperClasses.add(Class.forName(className, true, loader));
	}

	public void collect() throws IOException, URISyntaxException {

		for (final URL url : loader.getURLs()) {
			final File file = new File(url.toURI());
			if (!file.exists()) {
				continue;
			}
			final int lastDot = file.getName().lastIndexOf('.');
			if (file.isDirectory()) {
				final File folder = file.getAbsoluteFile().getCanonicalFile();
				handleDirectory(folder, folder.toString().length());
			} else if (lastDot > 0) {
				final String extension = file.getName().substring(lastDot);
				if (JAR_EXTENSIONS.contains(extension)) {
					handleJarFile(file);
				}
			}
		}

		final Iterator<Entry<Class<?>, Policy>> iterator = result.entrySet().iterator();
		while (iterator.hasNext()) {
			if (iterator.next().getValue() == null) {
				iterator.remove();
			}
		}
	}

	private void handleJarFile(final File file) throws IOException {
		// System.out.println("Handling jar file " + file);
		final JarFile jar = new JarFile(file);
		final Enumeration<JarEntry> entries = jar.entries();
		while (entries.hasMoreElements()) {
			final JarEntry jarEntry = entries.nextElement();
			if (jarEntry.getName().endsWith(CLASS_EXTENSION)) {
				handleClass(jarEntry.getName());
			}
		}
		jar.close();
	}

	private void handleDirectory(final File directory, final int baseLength) throws MalformedURLException {
		for (final File file : directory.listFiles()) {
			if (file.isDirectory()) {
				handleDirectory(file, baseLength);
			} else if (file.getName().endsWith(CLASS_EXTENSION)) {
				final String absolutePath = file.getAbsolutePath();
				final String className = absolutePath.substring(baseLength + 1);
				handleClass(className);
			}
		}

	}

	private void handleClass(final String classFileName) {
		// System.out.println("Handling " + classFileName);

		final String className = classFileName.substring(0, classFileName.length() - CLASS_EXTENSION.length())
				.replace('/', '.');
		try {
			final Class<?> clazz = Class.forName(className, true, loader);
			processClass(clazz);
		} catch (final Exception e) {
			// ignore
		} catch (final Error e) {
			// ignore
		}
	}

	private Policy match(final Class<?> clazz) {
		if (result.containsKey(clazz)) {
			return result.get(clazz);
		}
		if (expectedSuperClasses.contains(clazz)) {
			return Policy.ALL_SUBCLASSES;
		}
		for (final Annotation annotation : clazz.getAnnotations()) {
			final Policy policy = expectedTags.get(annotation.annotationType());
			if (policy != null) {
				return policy;
			}
		}
		return null;
	}

	private Policy processClass(final Class<?> clazz) {
		Policy policy = result.get(clazz);
		if (policy == null) {
			policy = match(clazz);
		}
		if (policy == null && clazz.getSuperclass() != null) {
			policy = processClass(clazz.getSuperclass());
		}
		if (policy == null) {
			for (final Class<?> iface : clazz.getInterfaces()) {
				policy = processClass(iface);
				if (policy != null) {
					break;
				}
			}
		}
		result.put(clazz, policy);
		return policy;
	}

	public Set<Class<?>> getResult() {
		return result.keySet();
	}
}
