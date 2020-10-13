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

import java.util.Collection;
import java.util.Set;

import ch.skymarshall.util.text.TextFormatter;

public class JavaCodeGenerator<E extends Exception> extends TextFormatter<JavaCodeGenerator<E>, E> {

	public interface InlinedCode<E extends Exception, F extends Exception> {
		void apply(JavaCodeGenerator<E> gen) throws F;
	}

	public static JavaCodeGenerator<RuntimeException> inMemory() {
		return new JavaCodeGenerator<>(output(new StringBuilder()));
	}

	public JavaCodeGenerator(IOutput<E> output) {
		super(output);
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

	public JavaCodeGenerator<E> openBlock(final String... extra) throws E {
		if (extra.length > 0) {
			appendIndent();
			for (final String str : extra) {
				append(str);
			}
		}
		return append(" {").indent().eol();
	}

	public JavaCodeGenerator<E> closeBlock(final String... extra) throws E {
		unindent().appendIndented("}");
		for (final String str : extra) {
			append(str);
		}
		return eol();
	}

	public JavaCodeGenerator<E> openIf(final String condition) throws E {
		return appendIndented("if (").append(condition).append(")").openBlock();
	}

	public JavaCodeGenerator<E> addVarAssign(final String type, final String name) throws E {
		return appendIndented(type).append(" ").append(name).append(" = ");
	}

	public JavaCodeGenerator<E> addVarDecl(final String scope, final String type, final String name) throws E {
		return appendIndented(String.format("%s %s %s", scope, type, name)).eos();
	}

	public JavaCodeGenerator<E> addVarDecl(final String scope, final String type, final String name, final String value)
			throws E {
		return appendIndented(String.format("%s %s %s = %s", scope, type, name, value)).eos();
	}

	public JavaCodeGenerator<E> addLocalVariable(final String type, final String name, final String value) throws E {
		return appendIndented(type).append(" ").append(name).append(" = ").append(value).eos();
	}

	public JavaCodeGenerator<E> appendMethodCall(final String instance, final String methodName,
			final Collection<String> parameters) throws E {
		return append("%s.%s(%s)", instance, methodName, String.join(",", parameters));
	}

	public JavaCodeGenerator<E> eos() throws E {
		return append(";").eol();
	}

	public <F extends Exception> JavaCodeGenerator<E> addMethodCall(final String methodName,
			final InlinedCode<E, F> inlinedParameters) throws E, F {
		append(methodName).append("(");
		inlinedParameters.apply(this);
		return append(")");
	}

	public <F extends Exception> JavaCodeGenerator<E> addMethodCall(final String instance, final String methodName,
			final InlinedCode<E, F> inlinedParameters) throws E, F {
		append(instance).append(".").append(methodName).append("(");
		inlinedParameters.apply(this);
		return append(")");
	}

	@Override
	public String toString() {
		return getOutput().toString();
	}

	public JavaCodeGenerator<E> addSetter(final String scope, final String type, final String property) throws E {
		appendIndented(String.format("%s void set%s(%s %s)", scope, toCamelCase(property), type, property)).openBlock();
		appendIndented(String.format("this.%s = %s", property, property)).eos();
		closeBlock();
		return this;
	}

}
