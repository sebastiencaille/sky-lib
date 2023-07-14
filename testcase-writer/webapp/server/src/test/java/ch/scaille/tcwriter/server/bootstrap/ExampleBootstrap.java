package ch.scaille.tcwriter.server.bootstrap;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import ch.scaille.generators.util.CodeGeneratorParams;
import ch.scaille.tcwriter.examples.ExampleHelper;
import ch.scaille.tcwriter.model.persistence.fsmodel.FsModelConfig;
import ch.scaille.tcwriter.testexec.JunitTestExecConfig;

public class ExampleBootstrap {

	private static final Path SRV_DATA = Paths.get("/var/lib/tcwriter/data");

	public static void main(String[] args) throws IOException {
		final var exampleHelper = new ExampleHelper(SRV_DATA, "server");
		final var model = exampleHelper.generateDictionary();
		final var tc = exampleHelper.recordTestCase(model);

		final var currentConfig = exampleHelper.getConfigDao().getCurrentConfig();
		currentConfig.getSubconfig(FsModelConfig.class).get().setTemplatePath("templates/TC.template");
		currentConfig.getSubconfig(JunitTestExecConfig.class).get()
				.setClasspath(CodeGeneratorParams.mavenTarget(ExampleHelper.class).resolve("classes").toString());

		exampleHelper.getModelDao().writeTestDictionary(model);
		exampleHelper.getModelDao().writeTestCase(ExampleHelper.TC_NAME, tc);
		exampleHelper.getConfigDao().saveConfiguration();
	}
}
