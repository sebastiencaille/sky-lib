package ch.scaille.tcwriter.maven;

import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.apache.maven.api.Language;
import org.apache.maven.api.model.FileSet;
import org.apache.maven.api.model.PatternSet;
import org.apache.maven.api.model.Source;
import org.apache.maven.api.services.PathMatcherFactory;
import org.apache.maven.api.di.Inject;
import org.apache.maven.api.ProjectScope;
import org.apache.maven.api.plugin.annotations.Mojo;
import org.apache.maven.api.plugin.annotations.Parameter;
import org.apache.maven.api.Project;
import org.apache.maven.api.services.ProjectManager;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import ch.scaille.generators.util.GenerationMetadata;
import ch.scaille.javabeans.DummyPropertiesGroup;
import ch.scaille.javabeans.properties.ObjectProperty;
import ch.scaille.tcwriter.javatc.generators.TestCaseToJava;
import ch.scaille.tcwriter.model.TestCaseException;
import ch.scaille.tcwriter.model.config.TCConfig;
import ch.scaille.tcwriter.persistence.ModelConfig;
import ch.scaille.tcwriter.persistence.ModelDao;
import ch.scaille.util.helpers.LambdaExt;
import ch.scaille.util.persistence.DaoFactory;
import ch.scaille.util.persistence.DaoFactory.FsDsFactory;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Mojo(name = "generateTestCases", defaultPhase = "generate-test-sources")
@NullMarked
@Slf4j
public class JavaTestCaseGeneratorMojo implements org.apache.maven.api.plugin.Mojo {

    public static class TCFileSet extends FileSet.Builder {

        public TCFileSet() {
            super(true);
        }
    }

    @Inject
    private Project project;

    @Inject
    private ProjectManager projectManager;

    @Inject
    protected PathMatcherFactory matcherFactory;


    @Parameter(property = "templatesFolder", defaultValue = "file:${project.testResources.testResource.directory}/userResources/templates")
    private String templatesFolder = "";

    @Parameter(property = "dictionaryFolder", defaultValue = "file:${project.testResources.testResource.directory}/dictionaries")
    private String dictionaryFolder = "";

    @Parameter(property = "dictionary")
    @Nullable
    private String dictionaryLocator = null;

    @Parameter(property = "testCases")
    @Nullable
    private List<TCFileSet> testCases = null;

    @Parameter(property = "outputFolder", defaultValue = "${project.build.directory}/generated-test-sources/tcwriter")
    private String outputFolder = "";

    private String resolve(String path) {
        if (!path.startsWith(DaoFactory.FS_DATASOURCE)) {
            return resolveFile(path).toString();
        }
        return DaoFactory.fs(resolveFile(path.substring(DaoFactory.FS_DATASOURCE.length())));
    }

    private Path resolveFile(String p) {
        final var path = Paths.get(p);
        if (path.isAbsolute()) {
            return path;
        }
        return project.getBasedir().resolve(p);
    }

    @SneakyThrows
    @Override
    public void execute() {
        // Defaults

/*
        if (testCases == null || testCases.isEmpty()) {
            // Implicitly scan for resource folder
            final var resource = Source.newBuilder()
                    .directory(resolve("src/test/resources/testcases"))
                    .includes(List.of("*.yaml"))
                    .enabled(true)
                    .build();
            testCases = List.of(resource);
        }*/
    	projectManager.addSourceRoot(project, ProjectScope.TEST, Language.JAVA_FAMILY, Paths.get(outputFolder));

        for (var testCaseSource: testCases.stream().map(TCFileSet::build).toList()) {
            // config folders and build model
            final var fsDsFactory = new FsDsFactory(Paths.get("."), false);
            final var daoFactory = DaoFactory.cpPlus(Set.of(), fsDsFactory);
            final var mavenModelConfig = new ModelConfig();
            mavenModelConfig.setDictionaryPath(resolve(dictionaryFolder));
            mavenModelConfig.setTcPath(resolve(testCaseSource.getDirectory()));
            mavenModelConfig.setTemplatePath(resolve(templatesFolder));
            mavenModelConfig.setTcExportPath("");
            final var config = new TCConfig("maven", List.of(mavenModelConfig));
            final var modelDao = new ModelDao(daoFactory,
                    new ObjectProperty<>("config", new DummyPropertiesGroup(), config),
                        fsDsFactory, ModelDao.defaultDataHandlers());

            final var sourceFilter = matcherFactory.createPathMatcher(Paths.get(testCaseSource.getDirectory()), testCaseSource.getIncludes(), testCaseSource.getExcludes());

            // Search test cases
            final var generator = new TestCaseToJava(modelDao);
                Files.walkFileTree(Paths.get(testCaseSource.getDirectory()), EnumSet.of(FileVisitOption.FOLLOW_LINKS), Integer.MAX_VALUE,
                    new SimpleFileVisitor<>() {
                        @Override
                        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                            if (sourceFilter.matches(file)) {
                                try {
                                    generateTestCase(generator, file, modelDao);
                                } catch (TestCaseException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                            return FileVisitResult.CONTINUE;
                        }
                    });
        }
    }

    private void generateTestCase(final TestCaseToJava generator,
                                  final Path tcFile,
                                  final ModelDao modelDao) throws TestCaseException {
        final var testCaseLocator = tcFile.getFileName().toString().split("\\.")[0];
        final var testMetadata = modelDao.loadTestCaseMetadata(testCaseLocator);
        final var dictionaries = modelDao.listDictionaries(testMetadata);
        if (dictionaries.isEmpty()) {
        	throw new IllegalStateException("No dictionary found for " + testCaseLocator + '/' + testMetadata);
        }
	    final var dictionaryLocatorToLoad = Objects.requireNonNullElseGet(dictionaryLocator, () -> dictionaries.getFirst().getTransientId());
        System.out.println("Dictionary: " + dictionaryLocatorToLoad);
        final var testCase = modelDao.readTestCase(testCaseLocator, modelDao.readTestDictionary(dictionaryLocatorToLoad)
                        .orElseThrow(() -> new IllegalStateException("Dictionary not found")))
                .orElseThrow(() -> new RuntimeException("Unable to find dictionary: " + dictionaryLocator));
        final var generationMetadata = new GenerationMetadata(JavaTestCaseGeneratorMojo.class,
                "dictionary=" + testCase.getDictionary());
        generator.generate(testCase, generationMetadata).writeTo(LambdaExt.uncheckedF2((file, src) -> {
            final var outputFile = resolveFile(outputFolder).resolve(file);
            Files.createDirectories(outputFile.getParent());
            Files.writeString(outputFile, src);
            return outputFile;
        }));
    }

}
