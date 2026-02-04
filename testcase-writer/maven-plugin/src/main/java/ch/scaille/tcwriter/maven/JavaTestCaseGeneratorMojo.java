package ch.scaille.tcwriter.maven;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import lombok.SneakyThrows;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.DirectoryScanner;

import ch.scaille.generators.util.GenerationMetadata;
import ch.scaille.javabeans.DummyPropertiesGroup;
import ch.scaille.javabeans.properties.ObjectProperty;
import ch.scaille.tcwriter.model.TestCaseException;
import ch.scaille.tcwriter.model.config.TCConfig;
import ch.scaille.tcwriter.persistence.ModelConfig;
import ch.scaille.tcwriter.persistence.ModelDao;
import ch.scaille.tcwriter.services.generators.TestCaseToJava;
import ch.scaille.util.helpers.LambdaExt;
import ch.scaille.util.persistence.DaoFactory;
import ch.scaille.util.persistence.DaoFactory.FsDsFactory;

@Mojo(name = "generateTestCases", defaultPhase = LifecyclePhase.GENERATE_TEST_SOURCES)
public class JavaTestCaseGeneratorMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project}", readonly = true)
    private MavenProject project;

    @Parameter(property = "templatesFolder", defaultValue = "file:${project.testResources.testResource.directory}/userResources/templates")
    private String templatesFolder;

    @Parameter(property = "dictionaryFolder", defaultValue = "file:${project.testResources.testResource.directory}/dictionaries")
    private String dictionaryFolder;

    @Parameter(property = "dictionary", required = false)
    private String dictionaryLocator;

    @Parameter(property = "testCases")
    private Resource testCases;

    @Parameter(property = "outputFolder", defaultValue = "${project.build.directory}/generated-test-sources/tcwriter")
    private String outputFolder;

    private String resolve(String path) {
        if (!path.startsWith(DaoFactory.FS_DATASOURCE)) {
            return path;
        }
        return DaoFactory.fs(resolveFile(path.substring(DaoFactory.FS_DATASOURCE.length())));
    }

    private String resolveFile(String p) {
        final var path = new File(p);
        if (path.isAbsolute()) {
            return path.toString();
        }
        return new File(project.getBasedir(), p).toString();
    }

    @SneakyThrows
    @Override
    public void execute() throws MojoExecutionException {

        // Defaults
        if (testCases == null || testCases.getDirectory() == null) {
            testCases = new Resource();
            testCases.setDirectory(resolveFile(project.getTestResources().getFirst().getDirectory() + "/testcases"));
            testCases.addInclude("*.yaml");
        }

        project.addTestCompileSourceRoot(outputFolder);

        final var tempFolder = Files.createTempDirectory("tcwriter-plugin");
        try {
            // config folders and build model
            final var fsDsFactory = new FsDsFactory(tempFolder);
            final var daoFactory = DaoFactory.cpPlus(Set.of(), fsDsFactory);
            final var mavenModelConfig = new ModelConfig();
            mavenModelConfig.setDictionaryPath(resolve(dictionaryFolder));
            mavenModelConfig.setTcPath(resolve(DaoFactory.fs(testCases.getDirectory())));
            mavenModelConfig.setTemplatePath(resolve(templatesFolder));
            mavenModelConfig.setTcExportPath("");
            final var config = new TCConfig("maven", List.of(mavenModelConfig));
            final var modelDao = new ModelDao(daoFactory,
                    new ObjectProperty<>("config", new DummyPropertiesGroup(), config), fsDsFactory, ModelDao::defaultDataHandlers);
            // Search test cases
            final var scanner = new DirectoryScanner();
            scanner.setBasedir(new File(testCases.getDirectory()));
            if (!testCases.getIncludes().isEmpty()) {
                scanner.setIncludes(testCases.getIncludes().toArray(new String[0]));
            }
            if (!testCases.getExcludes().isEmpty()) {
                scanner.setExcludes(testCases.getExcludes().toArray(new String[0]));
            }
            scanner.scan();

            getLog().debug("Scanning of: " + scanner.getBasedir().getAbsolutePath());
            getLog().info("Found: " + Arrays.asList(scanner.getIncludedFiles()));

            // Generate tests
            final var generator = new TestCaseToJava(modelDao);
            Arrays.stream(scanner.getIncludedFiles())
                    .forEach(LambdaExt
                            .uncheckedC(tcFile -> generateTestCase(generator, tcFile, modelDao)));
        } finally {
            // TODO recursive delete
            Files.deleteIfExists(tempFolder);
        }
    }

    private void generateTestCase(final TestCaseToJava generator,
                                  final String tcFile,
                                  final ModelDao modelDao) throws TestCaseException {
        final var testcaseLocator = tcFile.split("\\.")[0];
        final var testMetadata = modelDao.loadTestCaseMetadata(testcaseLocator);
        final var dictionaryLocatorToLoad = Objects.requireNonNull(dictionaryLocator, () -> modelDao.listDictionaries(testMetadata).get(0).getTransientId());
        final var testCase = modelDao.readTestCase(testcaseLocator, modelDao.readTestDictionary(dictionaryLocatorToLoad).get())
                .orElseThrow(() -> new RuntimeException("Unable to find dictionary: " + dictionaryLocator));
        final var generationMetadata = new GenerationMetadata(JavaTestCaseGeneratorMojo.class,
                "dictionary=" + testCase.getDictionary());
        generator.generate(testCase, generationMetadata).writeTo(LambdaExt.uncheckedF2((file, src) -> {
            final var outputFile = Paths.get(resolve(outputFolder)).resolve(file);
            getLog().info("Writing " + outputFile);
            Files.createDirectories(outputFile.getParent());
            Files.writeString(outputFile, src);
            return outputFile;
        }));
    }

}
