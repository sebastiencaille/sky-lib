package ch.scaille.gui.mvc;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import ch.scaille.annotations.GuiObject;
import ch.scaille.generators.util.CodeGeneratorParams;
import ch.scaille.generators.util.GenerationMetadata;
import ch.scaille.util.helpers.ClassFinder;
import ch.scaille.util.helpers.Logs;
import org.jspecify.annotations.NullMarked;

/**
 * To generate MVC model files, based on model classes 
 */
@NullMarked
public class GuiModelGenerator {

	public static void main(final String[] args) throws IOException {
		final var params = CodeGeneratorParams.parse(args);
		
		final var generationMetadata = GenerationMetadata.fromCommandLine(GuiModelGenerator.class, args);
		new GuiModelGenerator().process(params, generationMetadata);
	}

	private void process(final CodeGeneratorParams params, GenerationMetadata generationMetadata) throws IOException {
		final var targetFolder = Paths.get(params.getTargetFolder());
		Files.createDirectories(targetFolder);

		final var classPathFile = new File(params.getClassPathFolder());
		Logs.of(this).info("Scanning " + classPathFile.getAbsolutePath());
		try (var classFinder = ClassFinder.source(classPathFile)) {
			final var classes = classFinder.withAnnotation(GuiObject.class, ClassFinder.Policy.CLASS_ONLY)
					.withLibPackages(List.of("ch.scaille.javabeans.properties.", "ch.scaille.javabeans.persisters.",
							"ch.scaille.javabeans."))
					.withPackages(params.getScanPackage())
					.scan()
					.toList();
			Logs.of(this).info(() -> "Processing classes: " + classes);

			final var modelClassProcessor = new ModelClassProcessor(classFinder, params.getTargetPackage(), generationMetadata);
			for (final var clazz : classes) {
				modelClassProcessor.process(clazz).writeToFolder(targetFolder);
			}
		}

	}

}
