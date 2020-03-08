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
package ch.skymarshall.util.helpers;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * To select some classes according to an annotation, ...
 *
 * @author scaille
 *
 */
public class ClassFinder {

	private static final String[] DEFAULT_PACKAGES = { "", "ch.skymarshall.gui.mvc.properties.",
			"ch.skymarshall.gui.mvc.persisters.", "ch.skymarshall.gui." };

	public enum Policy {
		/**
		 * Gather all subclasses of the matching class
		 */
		ALL_SUBCLASSES,
		/**
		 * Gather the matching class only
		 */
		CLASS_ONLY
	}

	private static final String CLASS_EXTENSION = ".class";

	private static final Set<String> JAR_EXTENSIONS = new HashSet<>();
	static {
		JAR_EXTENSIONS.add(".jar");
		JAR_EXTENSIONS.add(".sar");
	}

	private final Map<Class<?>, Policy> collectedClasses = new HashMap<>();

	private final Map<Class<?>, Policy> expectedTags = new HashMap<>();

	private final Set<Class<?>> expectedSuperClasses = new HashSet<>();

	private final URLClassLoader loader;

	public static ClassFinder forApp() {
		return new ClassFinder(ClassLoaderHelper.appClassPath());
	}

	public ClassFinder(final URL[] urls) {
		this.loader = new URLClassLoader(urls);
		collectedClasses.put(Object.class, null);
	}

	public Class<?> loadByName(final String className) {

		Class<?> found = null;
		for (final String pkg : DEFAULT_PACKAGES) {
			try {
				found = loader.loadClass(pkg + className);
				break;
			} catch (final ClassNotFoundException e) {
				// ignore
			}
		}
		if (found == null) {
			throw new IllegalStateException("Not found: " + className);
		}
		return found;
	}

	public void addExpectedAnnotation(final Class<?> tag, final Policy policy) {
		if (!tag.isAnnotation()) {
			throw new IllegalArgumentException("Class is not an annotation: " + tag.getName());
		}
		expectedTags.put(tag, policy);
	}

	public void addExpectedSuperClass(final String className) throws ClassNotFoundException {
		expectedSuperClasses.add(Class.forName(className, true, loader));
	}

	public ClassFinder collect(final String basePackage) throws IOException {
		final Enumeration<URL> folders = loader.getResources(basePackage.replace(".", "/"));
		while (folders.hasMoreElements()) {
			scanFolder(folders.nextElement(), basePackage);
		}
		return this;
	}

	private void scanFolder(final URL resource, final String currentPackage) throws IOException {
		if (resource == null) {
			return;
		}
		try (InputStream in = resource.openStream()) {
			final String content = ClassLoaderHelper.readUTF8Resource(in);
			if (content.trim().isEmpty()) {
				return;
			}
			final String[] folderContent = content.split("\n");
			for (final String entry : folderContent) {
				if (entry.endsWith(CLASS_EXTENSION)) {
					handleClass(currentPackage + "." + entry.substring(0, entry.length() - CLASS_EXTENSION.length()));
				} else if (!entry.contains(".")) {
					// note: we may read files instead of folders
					scanFolder(new URL(resource.toString() + '/' + entry), currentPackage + '.' + entry);
				}
			}
		}
	}

	private void handleClass(final String className) {
		if ("module-info".equals(className)) {
			return;
		}
		try {
			processClass(Class.forName(className, false, loader));
		} catch (final Exception | NoClassDefFoundError e) { // NOSONAR
			// ignore
		}
	}

	private Policy match(final Class<?> clazz) {
		if (collectedClasses.containsKey(clazz)) {
			return collectedClasses.get(clazz);
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
		if (collectedClasses.containsKey(clazz)) {
			// already processed
			return collectedClasses.get(clazz);
		}
		Policy appliedPolicy = match(clazz);
		if (appliedPolicy == null && clazz.getSuperclass() != null) {
			appliedPolicy = processClass(clazz.getSuperclass());
			if (appliedPolicy == Policy.CLASS_ONLY) {
				// parent class policy is CLASS_ONLY, skip
				appliedPolicy = null;
			}
		}
		if (appliedPolicy == null) {
			for (final Class<?> iface : clazz.getInterfaces()) {
				appliedPolicy = processClass(iface);
				if (appliedPolicy != null) {
					break;
				}
			}
		}
		collectedClasses.put(clazz, appliedPolicy);
		return appliedPolicy;
	}

	public Set<Class<?>> getResult() {
		return collectedClasses.entrySet().stream().filter(e -> e.getValue() != null).map(Map.Entry::getKey)
				.collect(Collectors.toSet());
	}

}
