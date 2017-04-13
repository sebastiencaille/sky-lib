package ch.skymarshall.dataflowmgr.generator.exceptions;

import java.io.IOException;

public class GeneratorException extends RuntimeException {

	public GeneratorException(final String msg) {
		super(msg);
	}

	public GeneratorException(final String msg, final IOException e) {
		super(msg, e);
	}

}
