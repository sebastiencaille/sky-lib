package ch.scaille.tcwriter.gui;

import javax.swing.SwingUtilities;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import ch.scaille.tcwriter.gui.frame.TCWriterController;
import ch.scaille.tcwriter.javatc.testexec.JUnitTestExecutor;
import ch.scaille.tcwriter.model.config.TCConfig;
import ch.scaille.tcwriter.persistence.factory.DaoConfigs;
import ch.scaille.util.helpers.JavaExt;

public class TCEditor {

	public static class Args {
		@Parameter(names = { "-c" }, description = "Name of configuration")
		public String configuration = TCConfig.DEFAULT;

	}

	static void main(final String[] args) {
		final var mainArgs = new Args();
		JCommander.newBuilder().addObject(mainArgs).build().parse(args);

		final var daoConfig = DaoConfigs.withFolder(DaoConfigs.homeFolder());
		final var configLoader = daoConfig.configDao().setConfiguration(mainArgs.configuration);
		final var modelDao = daoConfig.modelDao();
		
		final var testJarPath = JavaExt.locationOf(TCEditor.class).resolve("../javatc-resources");
		final var testExecutor = new JUnitTestExecutor(configLoader, modelDao, 
				testJarPath.resolve("test-client.jar"));
		final var tcWriterController = new TCWriterController(configLoader, modelDao, null, testExecutor);
		SwingUtilities.invokeLater(tcWriterController::start);
	}

}
