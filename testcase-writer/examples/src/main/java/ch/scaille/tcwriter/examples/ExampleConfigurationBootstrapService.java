package ch.scaille.tcwriter.examples;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

import ch.scaille.tcwriter.persistence.ModelConfig;
import ch.scaille.tcwriter.persistence.factory.DaoConfigs;
import ch.scaille.tcwriter.persistence.testexec.JunitTestExecConfig;
import ch.scaille.testing.api.TestBootstrapService;
import ch.scaille.util.helpers.JavaExt;

public class ExampleConfigurationBootstrapService implements TestBootstrapService {

	
	private String TC_TEMPLATES_FOLDER = "templates";

	@Override
	public void bootstrap(Path dataFolder) {
		try {
		System.err.println("Bootstrapping data");
		final var exampleHelper = new ExampleHelper(dataFolder, "server");
		final var dictionary = exampleHelper.generateDictionary();
		final var tc = exampleHelper.recordTestCase(dictionary);

		// Sets up the test execution
		final var currentConfig = Objects.requireNonNull(exampleHelper.getConfigDao().getCurrentConfig());
		currentConfig.getSubconfig(ModelConfig.class).orElseThrow().setTemplatePath(TC_TEMPLATES_FOLDER);
		currentConfig.getSubconfig(JunitTestExecConfig.class).orElseThrow()
				.setClasspath(JavaExt.locationOf(TestBootstrapService.class).toString()
						.replaceFirst("WEB-INF.*", "example_resources/testcase-writer-examples-fatjar.jar"));
		exampleHelper.getConfigDao().saveConfiguration();
		
		exampleHelper.getModelDao().writeTestDictionary(dictionary);
		exampleHelper.getModelDao().writeTestCase(ExampleHelper.TC_NAME, tc);

		// Copy the default template
		final var templatePath = dataFolder.resolve(TC_TEMPLATES_FOLDER);
		Files.createDirectories(templatePath);
		for (var template : List.of("SimpleTest-java.template", "WebSearchTest-java.template")) {
			try (var in = Thread.currentThread().getContextClassLoader()
					.getResourceAsStream(DaoConfigs.USER_RESOURCES + TC_TEMPLATES_FOLDER + "/" + template);
					var out = new FileOutputStream(templatePath.toFile() + "/" + template)) {
				if (in == null) {
					throw new IllegalStateException("Template file not found");
				}
				in.transferTo(out);
			}
		}
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}
}
