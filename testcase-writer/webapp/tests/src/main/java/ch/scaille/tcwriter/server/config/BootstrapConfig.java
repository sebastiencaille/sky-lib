package ch.scaille.tcwriter.server.config;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import ch.scaille.generators.util.CodeGeneratorParams;
import ch.scaille.tcwriter.examples.ExampleHelper;
import ch.scaille.tcwriter.persistence.ModelConfig;
import ch.scaille.tcwriter.persistence.factory.DaoConfigs;
import ch.scaille.tcwriter.persistence.testexec.JunitTestExecConfig;
import jakarta.annotation.PostConstruct;

@Configuration
public class BootstrapConfig {

    private static final String TC_TEMPLATES_FOLDER = "templates";

    private final Path dataFolder;

    public BootstrapConfig(@Value("${app.dataFolder:#{systemProperties['user.home'] + '/.var/lib/tcwriter/data'}}") Path dataFolder) {
        this.dataFolder = dataFolder;
    }

    @PostConstruct
    public void bootStrapDemo() throws IOException {
        final var exampleHelper = new ExampleHelper(dataFolder, "server");
        final var dictionary = exampleHelper.generateDictionary();
        final var tc = exampleHelper.recordTestCase(dictionary);

        // Sets up the test execution
        final var currentConfig = exampleHelper.getConfigDao().getCurrentConfig();
        currentConfig.getSubconfig(ModelConfig.class).orElseThrow().setTemplatePath(TC_TEMPLATES_FOLDER);
        currentConfig.getSubconfig(JunitTestExecConfig.class)
                .orElseThrow()
                .setClasspath(CodeGeneratorParams.locationOf(ExampleHelper.class).toString());

        exampleHelper.getConfigDao().saveConfiguration();
        exampleHelper.getModelDao().writeTestDictionary(dictionary);
        exampleHelper.getModelDao().writeTestCase(ExampleHelper.TC_NAME, tc);

        // Copy the default template
        final var templatePath = dataFolder.resolve(TC_TEMPLATES_FOLDER);
        Files.createDirectories(templatePath);
        for (var template : List.of("SimpleTest-java.template", "WebSearchTest-java.template")) {
            try (var in = Thread.currentThread()
                    .getContextClassLoader()
                    .getResourceAsStream(DaoConfigs.USER_RESOURCES + TC_TEMPLATES_FOLDER + "/" + template);
                 var out = new FileOutputStream(templatePath.toFile() + "/" + template)) {
                if (in == null) {
                    throw new IllegalStateException("Template file not found");
                }
                in.transferTo(out);
            }
        }

    }

}
