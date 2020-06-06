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
import java.util.Collection;
import java.util.Set;

import ch.skymarshall.util.text.TextFormatter;

public class JavaCodeGenerator extends TextFormatter<JavaCodeGenerator> {

	public interface InlinedCode<E extends Exception> {
		void apply(JavaCodeGenerator gen) throws IOException, E;
	}

	public JavaCodeGenerator() {
		super(output(new StringBuilder()));
	}

	public static String classToSource(final String packageName, final String className) {
		return packageName.replace('.', '/') + '/' + className + ".java";
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

	public JavaCodeGenerator openBlock(final String... extra) throws IOException {
		if (extra.length > 0) {
			appendIndent();
			for (final String str : extra) {
				append(str);
			}
		}
		return append(" {").indent().eol();
	}

	public JavaCodeGenerator closeBlock(final String... extra) throws IOException {
		unindent().appendIndented("}");
		for (final String str : extra) {
			append(str);
		}
		return eol();
	}

	public JavaCodeGenerator openIf(final String condition) throws IOException {
		return appendIndented("if (").append(condition).append(")").openBlock();
	}

	public JavaCodeGenerator addVarAssign(final String type, final String name) throws IOException {
		return appendIndented(type).append(" ").append(name).append(" = ");
	}

	public JavaCodeGenerator addVarDecl(final String scope, final String type, final String name) throws IOException {
		return appendIndented(String.format("%s %s %s", scope, type, name)).eos();
	}

	public JavaCodeGenerator addVarDecl(final String scope, final String type, final String name, final String value)
			throws IOException {
		return appendIndented(String.format("%s %s %s = %s", scope, type, name, value)).eos();
	}

	public JavaCodeGenerator addLocalVariable(final String type, final String name, final String value)
			throws IOException {
		return appendIndented(type).append(" ").append(name).append(" = ").append(value).eos();
	}

	public JavaCodeGenerator appendMethodCall(final String instance, final String methodName,
			final Collection<String> parameters) throws IOException {
		return append("%s.%s(%s)", instance, methodName, String.join(",", parameters));
	}

	public JavaCodeGenerator eos() throws IOException {
		return append(";").eol();
	}

	public <E extends Exception> JavaCodeGenerator addMethodCall(final String methodName,
			final InlinedCode<E> inlinedParameters) throws IOException, E {
		append(methodName).append("(");
		inlinedParameters.apply(this);
		return append(")");

	}

	public <E extends Exception> JavaCodeGenerator addMethodCall(final String instance, final String methodName,
			final InlinedCode<E> inlinedParameters) throws IOException, E {
		append(instance).append(".").append(methodName).append("(");
		inlinedParameters.apply(this);
		return append(")");
	}

	@Override
	public String toString() {
		return getOutput().toString();
	}

	public JavaCodeGenerator addSetter(final String scope, final String type, final String property)
			throws IOException {
		appendIndented(String.format("%s void set%s(%s %s)", scope, toCamelCase(property), type, property)).openBlock();
		appendIndented(String.format("this.%s = %s", property, property)).eos();
		closeBlock();
		return this;
	}

}
