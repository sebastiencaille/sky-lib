package ch.scaille.generators.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.jspecify.annotations.NullMarked;

@NullMarked
public record GenerationMetadata (
	 String generator,
	 String commandLine,
	 String generationDate) {

	public static GenerationMetadata fromCommandLine(Class<?> generator, String[] commandLineArgs) {
		return new GenerationMetadata(generator, String.join(" ", commandLineArgs));
	}

	public GenerationMetadata(Class<?> generator, String commandLine) {
		this(generator.getName(), commandLine, new SimpleDateFormat("yyyy/MM/dd HH:mm").format(new Date()));
	}

}
