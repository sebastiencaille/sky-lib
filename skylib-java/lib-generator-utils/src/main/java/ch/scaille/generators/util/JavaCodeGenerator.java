package ch.scaille.generators.util;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import ch.scaille.util.text.TextFormatter;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class JavaCodeGenerator<E extends Exception> extends TextFormatter<JavaCodeGenerator<E>, E> {

	public interface InlinedCode<E extends Exception, F extends Exception> {
		void apply(JavaCodeGenerator<E> gen) throws F;
	}

	public static JavaCodeGenerator<RuntimeException> inMemory() {
		return new JavaCodeGenerator<>(output(new StringBuilder(1000)));
	}

	public JavaCodeGenerator(IOutput<E> output) {
		super(output);
	}

	public static String toSourceFilename(final String packageName, final String className) {
		return packageName.replace('.', '/') + '/' + className + ".java";
	}

	public static String simpleNameOf(String c) {
		return c.substring(c.lastIndexOf('.') + 1);
	}

	/**
	 * Generate the name of a constant
	 * @return a name compatible with a constant
	 */
	public static String toConstant(final String originalName) {
		final var builder = new StringBuilder(originalName.length());
		var prevUpper = 0;
		var prevIsNumeric = true;
		var isFirst = true;
		for (final var c : originalName.toCharArray()) {
			final var isUpper = Character.isUpperCase(c);
			final var isNumeric = Character.isDigit(c);
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
		return toImport.stream().map(p -> "import " + p + ';').collect(Collectors.joining("\n"));
	}

	public JavaCodeGenerator<E> openBlock(final String... extra) throws E {
		if (extra.length > 0) {
			appendIndent();
			append(String.join("", extra));
		}
		return append(" {").indent().eol();
	}

	public JavaCodeGenerator<E> closeBlock(final String... extra) throws E {
		unindent().appendIndented("}");
		append(String.join("", extra));
		return eol();
	}

	public JavaCodeGenerator<E> openIf(final String condition) throws E {
		return appendIndented("if (").append(condition).append(")").openBlock();
	}

	public JavaCodeGenerator<E> addVarAssign(final String type, final String name) throws E {
		return appendIndented(type).append(" ").append(name).append(" = ");
	}

	public JavaCodeGenerator<E> addInstanceVarDecl(final String modifiers, final String type, final String name)
			throws E {
		return appendIndented(String.format("%s %s %s", modifiers, type, name)).eos();
	}

	public JavaCodeGenerator<E> addInstanceVarDecl(final String modifiers, final String type, final String name,
			final String value) throws E {
		return appendIndented(String.format("%s %s %s = %s", modifiers, type, name, value)).eos();
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

	public JavaCodeGenerator<E> addSetter(final String modifiers, final String type, final String property) throws E {
		appendIndented(String.format("%s void set%s(%s %s)", modifiers, toCamelCase(property), type, property))
				.openBlock();
		appendIndented(String.format("this.%s = %s", property, property)).eos();
		closeBlock();
		return this;
	}

}
