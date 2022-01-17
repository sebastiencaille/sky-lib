package ch.scaille.tcwriter.gui;

import java.io.IOException;

import javax.swing.SwingUtilities;

import ch.scaille.tcwriter.executors.ITestExecutor;
import ch.scaille.tcwriter.executors.JunitTestExecutor;
import ch.scaille.tcwriter.generators.TCConfig;
import ch.scaille.tcwriter.generators.model.persistence.IModelPersister;
import ch.scaille.tcwriter.generators.model.persistence.JsonModelPersister;
import ch.scaille.tcwriter.gui.frame.TCWriterController;
import ch.scaille.util.helpers.ClassLoaderHelper;

public class TCEditor {

	public static void main(final String[] args) throws IOException {

		final IModelPersister persister = new JsonModelPersister();
		TCConfig config = persister.getConfiguration();
		if (args.length >= 1) {
			config = persister.readConfiguration(args[0]);
			persister.setConfiguration(config);
		}

		final ITestExecutor testExecutor = new JunitTestExecutor(persister, ClassLoaderHelper.appClassPath());
		final TCWriterController tcWriterController = new TCWriterController(persister, testExecutor);
		SwingUtilities.invokeLater(tcWriterController::run);
	}

}
