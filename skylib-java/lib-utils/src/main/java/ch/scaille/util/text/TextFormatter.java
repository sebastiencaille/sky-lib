package ch.scaille.util.text;

import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

/**
 * To output formatted text.
 * <p>
 *
 * @author Sebastien Caille
 *
 * @param <T> the concrete type, used to chain calls
 * @param <E> the output's Exception type
 *
 */
@SuppressWarnings("unchecked")
public class TextFormatter<T extends TextFormatter<T, E>, E extends Exception> {

	public interface IOutput<E extends Exception> {
		void append(String str) throws E;

		void append(char c) throws E;
		
		default byte[] getUTF8() {
			return toString().getBytes(StandardCharsets.UTF_8);
		}
	}

	public static IOutput<RuntimeException> output(final StringBuilder builder) {
		return new IOutput<>() {

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
		return new IOutput<>() {

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

	public static IOutput<RuntimeException> safeOutput(final Writer writer) {
		return new IOutput<>() {

			@Override
			public void append(final char c) {
				try {
					writer.write(c);
				} catch (IOException e) {
					throw new IllegalStateException("Unable to write into stream", e);
				}
			}

			@Override
			public void append(final String str) {
				try {
					writer.write(str);
				} catch (IOException e) {
					throw new IllegalStateException("Unable to write into stream", e);
				}
			}
		};
	}

	@Setter
    private IIndentationManager indentationManager = new CharIndentationManager();
	@Getter
    private final IOutput<E> output;

	public TextFormatter(final IOutput<E> output) {
		this.output = output;
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
		final var manager = indent();
		indentedExecution.accept(manager);
		return unindent();
	}

	public T appendIndent() throws E {
		return append(indentationManager.getIndentation());
	}

	public T append(final String str) throws E {
		output.append(str.replace("\n", "\n" + indentationManager.getIndentation()));
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

	public String currentIndentation() {
		return indentationManager.getIndentation();
	}

    public static String toCamelCase(final String s) {
		final var b = new StringBuilder();
		final var parts = s.split("_");
		for (final var part : parts) {
			b.append(Character.toUpperCase(part.charAt(0)));
			b.append(part.substring(1));
		}
		return b.toString();
	}

}
