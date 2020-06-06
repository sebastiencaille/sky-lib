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
package ch.skymarshall.util.text;

import java.io.IOException;
import java.io.OutputStream;

/**
 * To output formatted text.
 * <p>
 *
 * @author Sebastien Caille
 *
 */
public class TextFormatter<T extends TextFormatter<T>> {

	public interface IOutput {
		void append(String str) throws IOException;

		void append(char c) throws IOException;
	}

	public static IOutput output(final StringBuilder builder) {
		return new IOutput() {

			@Override
			public void append(final char c) {
				builder.append(c);
			}

			@Override
			public void append(final String str) {
				builder.append(str);
			}

			@Override
			public String toString() {
				return builder.toString();
			}
		};
	}

	public static IOutput output(final OutputStream stream) {
		return new IOutput() {

			@Override
			public void append(final char c) throws IOException {
				stream.write((byte) c);
			}

			@Override
			public void append(final String str) throws IOException {
				stream.write(str.getBytes());
			}
		};
	}

	private IIndentationManager indentationManager = new CharIndentationManager();
	private final IOutput output;

	public TextFormatter(final IOutput output) {
		this.output = output;
	}

	public void setIndentationManager(final IIndentationManager indentationManager) {
		this.indentationManager = indentationManager;
	}

	public T indent() {
		indentationManager.indent();
		return (T) this;
	}

	public T unindent() {
		indentationManager.unindent();
		return (T) this;
	}

	public T appendIndent() throws IOException {
		return append(indentationManager.getIndentation());
	}

	public T append(final String str) throws IOException {
		output.append(str);
		return (T) this;
	}

	public T appendIndented(final String string) throws IOException {
		output.append(indentationManager.getIndentation());
		return append(string);
	}

	public T appendAtOverride() throws IOException {
		output.append(indentationManager.getIndentation());
		return append("@Override\n");
	}

	public T appendIndentedLine(final String string) throws IOException {
		output.append(indentationManager.getIndentation());
		output.append(string);
		output.append('\n');
		return (T) this;
	}

	public T append(final String format, final Object... parameters) throws IOException {
		return append(String.format(format, parameters));
	}

	public T appendIndented(final String format, final Object... parameters) throws IOException {
		return appendIndented(String.format(format, parameters));
	}

	public T appendIndentedLine(final String format, final Object... parameters) throws IOException {
		return appendIndentedLine(String.format(format, parameters));
	}

	public T append(final TextFormatter<?> text) throws IOException {
		return append(text.getOutput().toString());
	}

	public T eol() throws IOException {
		output.append('\n');
		return (T) this;
	}

	public T eoli() throws IOException {
		output.append('\n');
		return appendIndent();
	}

	public T append(final StringBuilder builder) throws IOException {
		return append(builder.toString());
	}

	public IOutput getOutput() {
		return output;
	}

	public static String toCamelCase(final String s) {
		final StringBuilder b = new StringBuilder();
		final String[] parts = s.split("_");
		for (final String part : parts) {
			b.append(Character.toString(Character.toUpperCase(part.charAt(0))));
			b.append(part.substring(1));
		}
		return b.toString();
	}
}
