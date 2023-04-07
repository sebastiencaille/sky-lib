package ch.scaille.tcwriter.gui;

import javax.swing.SwingUtilities;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import ch.scaille.tcwriter.config.FsConfigManager;
import ch.scaille.tcwriter.config.TCConfig;
import ch.scaille.tcwriter.testexec.JUnitTestExecutor;
import ch.scaille.tcwriter.gui.frame.TCWriterController;
import ch.scaille.tcwriter.model.persistence.FsModelDao;
import ch.scaille.util.helpers.ClassLoaderHelper;

public class TCEditor {

	public static class Args {
		@Parameter(names = { "-c" }, description = "Name of configuration")
		public String configuration = TCConfig.DEFAULT;

	}
	
	public static void main(final String[] args) {
		final var mainArgs = new Args();
		JCommander.newBuilder().addObject(mainArgs).build().parse(args);
		
		final var configLoader = FsConfigManager.local().setConfiguration(mainArgs.configuration);
		final var modelDao = new FsModelDao(configLoader);
		final var testExecutor = new JUnitTestExecutor(configLoader, modelDao, ClassLoaderHelper.appClassPath());
		final var tcWriterController = new TCWriterController(configLoader, modelDao, null, testExecutor);
		SwingUtilities.invokeLater(tcWriterController::run);
	}

}
