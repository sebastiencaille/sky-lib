package ch.scaille.tcwriter.gui;

import javax.swing.SwingUtilities;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import ch.scaille.tcwriter.config.FsConfigManager;
import ch.scaille.tcwriter.config.TCConfig;
import ch.scaille.tcwriter.executors.JUnitTestExecutor;
import ch.scaille.tcwriter.gui.frame.TCWriterController;
import ch.scaille.tcwriter.model.persistence.FsModelDao;
import ch.scaille.util.helpers.ClassLoaderHelper;

public class TCEditor {

	public static class Args {
		@Parameter(names = { "-c" }, description = "Name of configuration")
		public String configuration = TCConfig.DEFAULT;

	}
	
	public static void main(final String[] args) {
		var mainArgs = new Args();
		JCommander.newBuilder().addObject(mainArgs).build().parse(args);
		
		var configLoader = FsConfigManager.local().setConfiguration(mainArgs.configuration);
		var modelDao = new FsModelDao(configLoader);
		var testExecutor = new JUnitTestExecutor(modelDao, ClassLoaderHelper.appClassPath());
		var tcWriterController = new TCWriterController(configLoader, modelDao, null, testExecutor);
		SwingUtilities.invokeLater(tcWriterController::run);
	}

}
