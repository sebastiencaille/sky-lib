package ch.scaille.generators.util;

import org.jspecify.annotations.NullMarked;

@NullMarked
public class TemplateException extends GeneratorException {

	public TemplateException(final String msg) {
		super(msg);
	}

}
