package ch.scaille.tcwriter.server.bootstrap;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import ch.scaille.generators.util.CodeGeneratorParams;
import ch.scaille.tcwriter.examples.ExampleHelper;
import ch.scaille.tcwriter.persistence.ModelConfig;
import ch.scaille.tcwriter.persistence.factory.DaoConfigs;
import ch.scaille.tcwriter.persistence.testexec.JunitTestExecConfig;

public class ExampleBootstrap {

	private static final String TC_TEMPLATE = "templates/TC.template";
	private static final Path SRV_DATA = Paths.get("/var/lib/tcwriter/data");

	public static void main(String[] args) throws IOException {
		final var exampleHelper = new ExampleHelper(SRV_DATA, "server");
		final var dictionary = exampleHelper.generateDictionary();
		final var tc = exampleHelper.recordTestCase(dictionary);

		// Setup the test execution
		final var currentConfig = exampleHelper.getConfigDao().getCurrentConfig();
		currentConfig.getSubconfig(ModelConfig.class).orElseThrow().setTemplatePath(TC_TEMPLATE);
		currentConfig.getSubconfig(JunitTestExecConfig.class)
				.orElseThrow()
				.setClasspath(CodeGeneratorParams.locationOf(ExampleHelper.class).toString());

		exampleHelper.getConfigDao().saveConfiguration();
		exampleHelper.getModelDao().writeTestDictionary(dictionary);
		exampleHelper.getModelDao().writeTestCase(ExampleHelper.TC_NAME, tc);

		// Copy the default template
		final var templatePath = SRV_DATA.resolve(TC_TEMPLATE);
		Files.createDirectories(templatePath.getParent());
		try (var in = Thread.currentThread()
				.getContextClassLoader()
				.getResourceAsStream(DaoConfigs.USER_RESOURCES + TC_TEMPLATE);
				var out = new FileOutputStream(templatePath.toFile())) {
			in.transferTo(out);
		}

	}
}
