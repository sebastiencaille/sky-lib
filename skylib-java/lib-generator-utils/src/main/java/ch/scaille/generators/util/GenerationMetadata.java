package ch.scaille.generators.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class GenerationMetadata {
	private final String generator;
	private final String comments;
	private final String generationDate;

	public static GenerationMetadata fromCommandLine(Class<?> generator, String[] commandLineArgs) {
		return new GenerationMetadata(generator, String.join(" ", commandLineArgs));
	}

	public GenerationMetadata(Class<?> generator, String comments) {
		this.generator = generator.getName();
		this.comments = comments;
		this.generationDate = new SimpleDateFormat("yyyy/MM/dd HH:mm").format(new Date());
	}

	public String getGenerator() {
		return generator;
	}

	public String getComments() {
		return comments;
	}

	public String getGenerationDate() {
		return generationDate;
	}
}
