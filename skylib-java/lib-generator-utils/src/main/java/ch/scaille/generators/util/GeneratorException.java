package ch.scaille.generators.util;

import org.jspecify.annotations.NullMarked;

import java.io.IOException;

@NullMarked
public class GeneratorException extends RuntimeException {

	public GeneratorException(final String msg) {
		super(msg);
	}

	public GeneratorException(final String msg, final IOException e) {
		super(msg, e);
	}

}
