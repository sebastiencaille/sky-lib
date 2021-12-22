package ch.scaille.util.text;

public class SimpleTextFormatter<E extends Exception> extends TextFormatter<SimpleTextFormatter<E>, E> {

	public SimpleTextFormatter(IOutput<E> output) {
		super(output);
	}

	public static SimpleTextFormatter<RuntimeException> inMemory() {
		return new SimpleTextFormatter<>(TextFormatter.output(new StringBuilder()));
	}

}
