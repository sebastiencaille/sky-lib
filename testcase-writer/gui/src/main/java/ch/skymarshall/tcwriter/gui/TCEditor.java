package ch.skymarshall.tcwriter.gui;

import java.io.IOException;

import javax.swing.SwingUtilities;

import ch.skymarshall.tcwriter.generators.GeneratorConfig;
import ch.skymarshall.tcwriter.generators.model.persistence.IModelPersister;
import ch.skymarshall.tcwriter.generators.model.persistence.JsonModelPersister;
import ch.skymarshall.tcwriter.gui.frame.TCWriterController;
import ch.skymarshall.util.helpers.ClassLoaderHelper;
import executors.ITestExecutor;
import executors.JunitTestExecutor;

public class TCEditor {

	public static void main(final String[] args) throws IOException {

		final IModelPersister persister = new JsonModelPersister();
		GeneratorConfig config = new GeneratorConfig();
		if (args.length >= 1) {
			config = persister.readConfiguration(args[0]);
		}
		persister.setConfiguration(config);

		final ITestExecutor testExecutor = new JunitTestExecutor(config, ClassLoaderHelper.appClassPath());
		final TCWriterController tcWriterController = new TCWriterController(config, persister, testExecutor);
		SwingUtilities.invokeLater(tcWriterController::run);
	}

}
