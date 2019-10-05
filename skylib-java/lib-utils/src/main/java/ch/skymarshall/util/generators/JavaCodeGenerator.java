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
package ch.skymarshall.util.generators;

import java.io.IOException;
import java.util.Set;

import ch.skymarshall.util.text.TextFormatter;

public class JavaCodeGenerator extends TextFormatter {

	public JavaCodeGenerator() {
		super(output(new StringBuilder()));
	}

	@Override
	public String toString() {
		return getOutput().toString();
	}

	public static String toFirstLetterInUpperCase(final String str) {
		if (str == null) {
			return null;
		}
		return Character.toUpperCase(str.charAt(0)) + str.substring(1);
	}

	public static String toFirstLetterInLowerCase(final String str) {
		if (str == null) {
			return null;
		}
		return Character.toLowerCase(str.charAt(0)) + str.substring(1);
	}

	public static String toConstant(final String str) {
		final StringBuilder builder = new StringBuilder(str.length());
		int prevUpper = 0;
		boolean prevIsNumeric = true;
		boolean isFirst = true;
		for (final char c : str.toCharArray()) {
			final boolean isUpper = Character.isUpperCase(c);
			final boolean isNumeric = Character.isDigit(c);
			if (isFirst) {
				isFirst = false;
			} else if (isNumeric ^ prevIsNumeric || (isUpper && prevUpper == 0)) {
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

	public static String toImports(final Set<String> toImport) {
		final StringBuilder imports = new StringBuilder();
		for (final String imp : toImport) {
			imports.append("import ").append(imp).append(";\n");
		}
		return imports.toString();
	}

	public void openBlock(final String... extra) throws IOException {
		appendIndented("");
		for (final String str : extra) {
			append(str);
		}
		append(" {").newLine().indent();
	}

	public void closeBlock(final String... extra) throws IOException {
		unindent().appendIndented("}");
		for (final String str : extra) {
			append(str);
		}
		newLine();
	}
}
