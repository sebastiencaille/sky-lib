package ch.scaille.tcwriter.server.bootstrap;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import ch.scaille.generators.util.CodeGeneratorParams;
import ch.scaille.tcwriter.examples.ExampleHelper;
import ch.scaille.tcwriter.persistence.fs.FsModelConfig;
import ch.scaille.tcwriter.persistence.testexec.JunitTestExecConfig;

public class ExampleBootstrap {

	private static final String TC_TEMPLATE = "templates/TC.template";
	private static final Path SRV_DATA = Paths.get("/var/lib/tcwriter/data");

	public static void main(String[] args) throws IOException {
		final var exampleHelper = new ExampleHelper(SRV_DATA, "server");
		final var model = exampleHelper.generateDictionary();
		final var tc = exampleHelper.recordTestCase(model);

		final var currentConfig = exampleHelper.getConfigDao().getCurrentConfig();
		currentConfig.getSubconfig(FsModelConfig.class).orElseThrow().setTemplatePath(TC_TEMPLATE);
		currentConfig.getSubconfig(JunitTestExecConfig.class)
				.orElseThrow()
				.setClasspath(CodeGeneratorParams.mavenTarget(ExampleHelper.class).resolve("classes").toString());

		exampleHelper.getModelDao().writeTestDictionary(model);
		exampleHelper.getModelDao().writeTestCase(ExampleHelper.TC_NAME, tc);
		exampleHelper.getConfigDao().saveConfiguration();

		final var templatePath = SRV_DATA.resolve(TC_TEMPLATE);
		Files.createDirectories(templatePath.getParent());
		try (var in = Thread.currentThread().getContextClassLoader().getResourceAsStream(TC_TEMPLATE);
				var out = new FileOutputStream(templatePath.toFile())) {
			in.transferTo(out);
		}

	}
}