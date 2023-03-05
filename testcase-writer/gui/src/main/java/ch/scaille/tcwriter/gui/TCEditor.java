package ch.scaille.tcwriter.gui;

import java.io.IOException;

import javax.swing.SwingUtilities;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import ch.scaille.tcwriter.executors.JUnitTestExecutor;
import ch.scaille.tcwriter.gui.frame.TCWriterController;
import ch.scaille.tcwriter.model.persistence.FsModelConfig;
import ch.scaille.tcwriter.model.persistence.FsModelDao;
import ch.scaille.util.helpers.ClassLoaderHelper;

public class TCEditor {

	public static class Args {
		@Parameter(names = { "-c" }, description = "Name of configuration")
		public String configuration = FsModelConfig.DEFAULT;

	}
	
	public static void main(final String[] args) throws IOException {
		var mainArgs = new Args();
		JCommander.newBuilder().addObject(mainArgs).build().parse(args);
		var modelDao = new FsModelDao(FsModelDao.loadConfiguration(mainArgs.configuration));
		var testExecutor = new JUnitTestExecutor(modelDao, ClassLoaderHelper.appClassPath());
		var tcWriterController = new TCWriterController(modelDao, null, testExecutor);
		SwingUtilities.invokeLater(tcWriterController::run);
	}

}
