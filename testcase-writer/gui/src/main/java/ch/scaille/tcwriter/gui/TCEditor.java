package ch.scaille.tcwriter.gui;

import java.io.IOException;

import javax.swing.SwingUtilities;

import ch.scaille.tcwriter.executors.JunitTestExecutor;
import ch.scaille.tcwriter.gui.frame.TCWriterController;
import ch.scaille.tcwriter.model.persistence.FsModelDao;
import ch.scaille.util.helpers.ClassLoaderHelper;

public class TCEditor {

	public static void main(final String[] args) throws IOException {

		var modelDao = FsModelDao.withDefaultConfig();
		if (args.length >= 1) {
			modelDao.loadConfiguration(args[0]);
		}
		var testExecutor = new JunitTestExecutor(modelDao, ClassLoaderHelper.appClassPath());
		var tcWriterController = new TCWriterController(modelDao, testExecutor);
		SwingUtilities.invokeLater(tcWriterController::run);
	}

}
