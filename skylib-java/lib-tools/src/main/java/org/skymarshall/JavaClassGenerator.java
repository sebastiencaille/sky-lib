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
package org.skymarshall;

import java.util.HashSet;
import java.util.Set;

import org.skymarshall.util.text.TextFormatter;

public class JavaClassGenerator extends TextFormatter {

	private String pkg;

	private final Set<String> imports = new HashSet<String>();

	public JavaClassGenerator() {
		super(TextFormatter.output(new StringBuilder()));
	}

	public void setPackage(final String pkg) {
		this.pkg = pkg;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("package ");
		builder.append(pkg);
		builder.append(";\n\n");

		for (final String imp : imports) {
			builder.append("import ");
			builder.append(imp);
			builder.append(";\n");
		}

		builder.append('\n');
		builder.append(getOutput().toString());

		return builder.toString();
	}

	public void addImport(final Class<?> class1) {
		if (class1.isArray() || class1.isPrimitive()) {
			return;
		}
		imports.add(class1.getName());
	}

	public void addImport(final String className) {
		imports.add(className);
	}

	public String toFirstLetterInUpperCase(final String str) {
		if (str == null) {
			return null;
		}
		return Character.toUpperCase(str.charAt(0)) + str.substring(1);
	}

	public String toFirstLetterInLowerCase(final String str) {
		if (str == null) {
			return null;
		}
		return Character.toLowerCase(str.charAt(0)) + str.substring(1);
	}

	public String toConstant(final String str) {
		final StringBuilder builder = new StringBuilder(str.length());
		int prevUpper = 0;
		boolean prevIsNumeric = true;
		boolean isFirst = true;
		for (final char c : str.toCharArray()) {
			final boolean isUpper = Character.isUpperCase(c);
			final boolean isNumeric = Character.isDigit(c);
			if (isFirst) {
				isFirst = false;
			} else if (isNumeric ^ prevIsNumeric) {
				builder.append('_');
			} else if (isUpper && prevUpper == 0) {
				builder.append('_');
			}
			builder.append(Character.toUpperCase(c));

			if (isUpper) {
				prevUpper++;
			} else {
				prevUpper = 0;
			}
			prevIsNumeric = isNumeric;
		}
		return builder.toString();
	}
}
