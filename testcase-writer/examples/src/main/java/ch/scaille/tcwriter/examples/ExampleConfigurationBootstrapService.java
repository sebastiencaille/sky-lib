package ch.scaille.tcwriter.examples;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

import ch.scaille.tcwriter.persistence.ModelConfig;
import ch.scaille.tcwriter.persistence.factory.DaoFactory;
import ch.scaille.tcwriter.persistence.testexec.JunitTestExecConfig;
import ch.scaille.testing.api.IConfigurationProvider;
import ch.scaille.testing.api.TestBootstrapService;
import lombok.extern.java.Log;

@Log
public class ExampleConfigurationBootstrapService implements TestBootstrapService {

	private static final String TC_TEMPLATES_FOLDER = "templates";

    @Override
	public void bootstrap(IConfigurationProvider configurationProvider) {
		try {

            final var dataFolder = Paths.get(configurationProvider.getConfiguration("tcwriter.data.folder")).toAbsolutePath();
            final var testingJarFolder = Paths.get(configurationProvider.getConfiguration("tcwriter.testJars.folder")).toAbsolutePath();

            log.info("Bootstrapping example data");
            log.info("Data folder: " + dataFolder);
            log.info("Testing jar folder: " + testingJarFolder);
            final var exampleFactory = new ExampleFactory(dataFolder, "server");
            final var dictionary = exampleFactory.generateDictionary();
            final var tc = exampleFactory.recordTestCase(dictionary);

            // Sets up the test execution
            final var currentConfig = Objects.requireNonNull(exampleFactory.getConfigDao().getCurrentConfig().getValue());
            currentConfig.getSubconfig(ModelConfig.class).orElseThrow().setTemplatePath(TC_TEMPLATES_FOLDER);
            currentConfig.getSubconfig(JunitTestExecConfig.class).orElseThrow()
                    .setClasspath(testingJarFolder.resolve("testcase-writer-examples-fatjar.jar").toString());
            exampleFactory.getConfigDao().saveConfiguration();

            exampleFactory.getModelDao().writeTestDictionary(dictionary);
            exampleFactory.getModelDao().writeTestCase(ExampleFactory.TC_NAME, tc);

            // Copy the default template
            final var templatePath = dataFolder.resolve(TC_TEMPLATES_FOLDER);
            Files.createDirectories(templatePath);
            for (var template : List.of("SimpleTest-java.template", "WebSearchTest-java.template")) {
                try (var in = Thread.currentThread().getContextClassLoader()
                        .getResourceAsStream(DaoFactory.USER_RESOURCES + TC_TEMPLATES_FOLDER + "/" + template);
                     var out = Files.newOutputStream(templatePath.resolve(template))) {
                    Objects.requireNonNull(in,"Template file not found").transferTo(out);
                }
            }
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}
}
