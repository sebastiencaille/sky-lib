package ch.scaille.generators.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;


public record GenerationMetadata (
	 String generator,
	 String commandLine,
	 String generationDate) {

	private static final DateTimeFormatter GENERATION_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

	public static GenerationMetadata fromCommandLine(Class<?> generator, String[] commandLineArgs) {
		return new GenerationMetadata(generator, String.join(" ", commandLineArgs));
	}

	public GenerationMetadata(Class<?> generator, String commandLine) {
		this(generator.getName(), commandLine, GENERATION_TIME_FORMAT.format(LocalDateTime.now(ZoneId.systemDefault())));
	}

}
