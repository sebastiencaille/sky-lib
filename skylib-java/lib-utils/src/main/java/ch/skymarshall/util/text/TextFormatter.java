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
import java.util.function.Consumer;

/**
 * To output formatted text.
 * <p>
 *
 * @author Sebastien Caille
 *
 * @param T the concrete type, used to chain calls
 * @param E the output's Exception type
 *
 */
public class TextFormatter<T extends TextFormatter<T, E>, E extends Exception> {

	public interface IOutput<E extends Exception> {
		void append(String str) throws E;

		void append(char c) throws E;
	}

	public static IOutput<RuntimeException> output(final StringBuilder builder) {
		return new IOutput<RuntimeException>() {

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

	public static IOutput<IOException> output(final OutputStream stream) {
		return new IOutput<IOException>() {

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

	public static IOutput<RuntimeException> safeOutput(final OutputStream stream) {
		return new IOutput<RuntimeException>() {

			@Override
			public void append(final char c) {
				try {
					stream.write((byte) c);
				} catch (IOException e) {
					throw new IllegalStateException("Unable to write into stream", e);
				}
			}

			@Override
			public void append(final String str) {
				try {
					stream.write(str.getBytes());
				} catch (IOException e) {
					throw new IllegalStateException("Unable to write into stream", e);
				}
			}
		};
	}

	private IIndentationManager indentationManager = new CharIndentationManager();
	private final IOutput<E> output;

	public TextFormatter(final IOutput<E> output) {
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

	public T indented(Consumer<T> indentedExecution) {
		T manager = indent();
		indentedExecution.accept(manager);
		return unindent();
	}

	public T appendIndent() throws E {
		return append(indentationManager.getIndentation());
	}

	public T append(final String str) throws E {
		output.append(str);
		return (T) this;
	}

	public T appendIndented(final String string) throws E {
		output.append(indentationManager.getIndentation());
		return append(string);
	}

	public T appendAtOverride() throws E {
		output.append(indentationManager.getIndentation());
		return append("@Override\n");
	}

	public T appendIndentedLine(final String string) throws E {
		output.append(indentationManager.getIndentation());
		output.append(string);
		output.append('\n');
		return (T) this;
	}

	public T append(final String format, final Object... parameters) throws E {
		return append(String.format(format, parameters));
	}

	public T appendIndented(final String format, final Object... parameters) throws E {
		return appendIndented(String.format(format, parameters));
	}

	public T appendIndentedLine(final String format, final Object... parameters) throws E {
		return appendIndentedLine(String.format(format, parameters));
	}

	public T append(final TextFormatter<?, E> text) throws E {
		return append(text.getOutput().toString());
	}

	public T eol() throws E {
		output.append('\n');
		return (T) this;
	}

	public T eoli() throws E {
		output.append('\n');
		return appendIndent();
	}

	public T append(final StringBuilder builder) throws E {
		return append(builder.toString());
	}

	public IOutput<E> getOutput() {
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
