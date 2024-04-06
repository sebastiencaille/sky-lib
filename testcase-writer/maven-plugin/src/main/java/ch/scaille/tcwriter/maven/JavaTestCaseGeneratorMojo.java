package ch.scaille.tcwriter.maven;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.DirectoryScanner;

import ch.scaille.generators.util.GenerationMetadata;
import ch.scaille.javabeans.DummyPropertiesGroup;
import ch.scaille.javabeans.properties.ObjectProperty;
import ch.scaille.tcwriter.model.TestCaseException;
import ch.scaille.tcwriter.model.config.TCConfig;
import ch.scaille.tcwriter.model.dictionary.TestDictionary;
import ch.scaille.tcwriter.persistence.ModelConfig;
import ch.scaille.tcwriter.persistence.ModelDao;
import ch.scaille.tcwriter.services.generators.TestCaseToJava;
import ch.scaille.util.helpers.LambdaExt;
import ch.scaille.util.persistence.DaoFactory;
import ch.scaille.util.persistence.DaoFactory.FsDsFactory;

@Mojo(name = "generateTestCases")
public class JavaTestCaseGeneratorMojo extends AbstractMojo {

	@Component
	private MavenProject project;

	@Parameter(property = "template", required = false, defaultValue = "file:src/test/resources/templates/TC.template")
	private String template;

	@Parameter(property = "dictionaryFolder", required = false, defaultValue = "file:src/test/resources/dictionaries")
	private String dictionaryFolder;

	@Parameter(property = "dictionary", required = false, defaultValue = "default")
	private String dictionaryLocator;

	@Parameter(property = "testCases", required = false)
	private Resource testCases;

	@Parameter(property = "outputFolder", required = false, defaultValue = "${project.build.directory}/generated-test-sources/tcwriter")
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

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {

		// Defaults
		if (testCases == null || testCases.getDirectory() == null) {
			testCases = new Resource();
			testCases.setDirectory(resolveFile("src/test/resources/testcases"));
			testCases.addInclude("*.yaml");
		}

		project.addTestCompileSourceRoot(outputFolder);
		
		// config folders and build model
		final var daoFactory = DaoFactory.cpPlus(Set.of(), new FsDsFactory(null));
		final var mavenModelConfig = new ModelConfig();
		mavenModelConfig.setDictionaryPath(resolve(dictionaryFolder));
		mavenModelConfig.setTcPath(resolve(DaoFactory.fs(testCases.getDirectory())));
		mavenModelConfig.setTemplatePath(resolve(template));
		mavenModelConfig.setTcExportPath("");
		final var config = new TCConfig("maven", List.of(mavenModelConfig));
		final var modelDao = new ModelDao(daoFactory,
				new ObjectProperty<>("config", new DummyPropertiesGroup(), config), ModelDao.defaultDataHandlers());
		try {
			// Search test cases
			final var scanner = new DirectoryScanner();
			scanner.setBasedir(new File(testCases.getDirectory()));
			scanner.setIncludes(testCases.getIncludes().toArray(new String[0]));
			scanner.setExcludes(testCases.getExcludes().toArray(new String[0]));
			scanner.scan();

			getLog().debug("Scanner of: " + scanner.getBasedir());
			getLog().info("Found: " + Arrays.asList(scanner.getIncludedFiles()));

			// Load dictionary
			final var testDictionary = modelDao.readTestDictionary(dictionaryLocator)
					.orElseThrow(() -> new FileNotFoundException(dictionaryLocator));

			// Generate tests
			final var generator = new TestCaseToJava(modelDao);
			Arrays.stream(scanner.getIncludedFiles())
					.forEach(LambdaExt
							.uncheckedC(tcFile -> generateTestCase(generator, tcFile, testDictionary, modelDao)));
		} catch (FileNotFoundException e) {
			throw new MojoExecutionException("Unable to load dictonary", e);
		}

	}

	private void generateTestCase(final TestCaseToJava generator, String tcFile, final TestDictionary testDictionary,
			final ModelDao modelDao) throws TestCaseException {
		final var generationMetadata = new GenerationMetadata(JavaTestCaseGeneratorMojo.class,
				"dictionary=" + testDictionary.getMetadata());
		final var testcaseLocator = tcFile.split("\\.")[0];
		final var testCase = modelDao.readTestCase(testcaseLocator, testDictionary)
				.orElseThrow(() -> new RuntimeException("Unable to find test case: " + testcaseLocator));
		generator.generate(testCase, generationMetadata).writeTo(LambdaExt.uncheckedF2((file, src) -> {
			final var outputFile = Paths.get(resolve(outputFolder)).resolve(file);
			getLog().info("Writing " + outputFile);
			Files.createDirectories(outputFile.getParent());
			Files.write(outputFile, src.getBytes(StandardCharsets.UTF_8));
			return outputFile;
		}));
	}

}
