package ch.scaille.gui.mvc;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import ch.scaille.annotations.GuiObject;
import ch.scaille.generators.util.CodeGeneratorParams;
import ch.scaille.util.helpers.ClassFinder;
import ch.scaille.util.helpers.Logs;

public class GuiModelGenerator {

	public static void main(final String[] args) throws IOException {
		final var params = CodeGeneratorParams.parse(args);
		new GuiModelGenerator().process(params);
	}

	private void process(final CodeGeneratorParams params) throws IOException {
		final var targetFolder = Paths.get(params.getTargetFolder());
		Files.createDirectories(targetFolder);

		Logs.of(this).info("Scanning " + params.getSourceFolder());
		try (var classFinder = ClassFinder.source(new File(params.getSourceFolder()))) {
			final var classes = classFinder.withAnnotation(GuiObject.class, ClassFinder.Policy.CLASS_ONLY)
					.withLibPackages(List.of("ch.scaille.javabeans.properties.", "ch.scaille.javabeans.persisters.",
							"ch.scaille.javabeans."))
					.withPackages(params.getNamespaceFilter())
					.scan()
					.collect(Collectors.toList());
			Logs.of(this).info(() -> "Processing classes: " + classes);

			for (final var clazz : classes) {
				new ModelClassProcessor(clazz, classFinder).process().writeToFolder(targetFolder);
			}
		}

	}

}
