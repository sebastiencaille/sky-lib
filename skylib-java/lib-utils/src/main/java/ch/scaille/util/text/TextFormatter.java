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
		IOutput<E> append(String str) throws E;

		IOutput<E> append(char c) throws E;
		
		default byte[] getUTF8() {
			return toString().getBytes(StandardCharsets.UTF_8);
		}
	}

	public static IOutput<RuntimeException> output(final StringBuilder builder) {
		return new IOutput<>() {

			@Override
			public IOutput<RuntimeException> append(final char c) {
				builder.append(c);
				return this;
			}

			@Override
			public IOutput<RuntimeException> append(final String str) {
				builder.append(str);
				return this;
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
			public IOutput<IOException> append(final char c) throws IOException {
				stream.write((byte) c);
				return this;
			}

			@Override
			public IOutput<IOException> append(final String str) throws IOException {
				stream.write(str.getBytes());
				return this;
			}
		};
	}

	public static IOutput<RuntimeException> safeOutput(final Writer writer) {
		return new IOutput<>() {

			@Override
			public IOutput<RuntimeException>  append(final char c) {
				try {
					writer.write(c);
					return this;
				} catch (IOException e) {
					throw new IllegalStateException("Unable to write into stream", e);
				}
			}

			@Override
			public IOutput<RuntimeException>  append(final String str) {
				try {
					writer.write(str);
					return this;
				} catch (IOException e) {
					throw new IllegalStateException("Unable to write into stream", e);
				}
			}
		};
	}

	public static IOutput<RuntimeException> memoryOutput() {
		return output(new StringBuilder(1000));
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

	public T appendQuoted(final String str) throws E {
		output.append('"').append(str).append('"');
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
		return append(format.formatted(parameters));
	}

	public T appendIndented(final String format, final Object... parameters) throws E {
		return appendIndented(format.formatted(parameters));
	}

	public T appendIndentedLine(final String format, final Object... parameters) throws E {
		return appendIndentedLine(format.formatted(parameters));
	}

	public T append(final TextFormatter<?, E> text) throws E {
		return append(text.getOutput().toString());
	}

	public T eol() throws E {
		output.append('\n');
		return (T) this;
	}

	/**
	 * write an eol + indent
	 */
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

    public static String snakeCaseToPascalCase(final String s) {
		final var b = new StringBuilder();
		final var parts = s.split("_");
		for (final var part : parts) {
			b.append(Character.toUpperCase(part.charAt(0)));
			b.append(part.substring(1));
		}
		return b.toString();
	}

	public static String snakeCaseToCamelCase(final String s) {
		final var result = snakeCaseToPascalCase(s);
		return Character.toLowerCase(result.charAt(0)) + result.substring(1);
	}


	public static String camelCaseToPascalCase(final String s) {
		return Character.toUpperCase(s.charAt(0)) + s.substring(1);
	}


}
