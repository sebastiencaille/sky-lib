package ch.scaille.tcwriter.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;

import ch.scaille.gui.swing.SwingHelper;
import ch.scaille.tcwriter.annotations.TCActors;
import ch.scaille.tcwriter.annotations.TCRole;
import ch.scaille.tcwriter.generators.JavaToDictionary;
import ch.scaille.tcwriter.gui.frame.TCWriterGui;
import ch.scaille.tcwriter.model.persistence.IModelDao;
import ch.scaille.util.helpers.ClassFinder;
import ch.scaille.util.helpers.ClassFinder.Policy;
import ch.scaille.util.helpers.ClassFinder.URLClassFinder;
import ch.scaille.util.helpers.LambdaExt;

public class DictionaryImport extends JDialog {

	private final IModelDao modelDao;
	private final Component parentFrame;
	private final JLabel dictionaryJarFileDisplay;
	private boolean imported = true;

	public DictionaryImport(Component parentFrame, IModelDao modelDao) {
		this.parentFrame = parentFrame;
		this.modelDao = modelDao;
		setLayout(new BorderLayout());

		dictionaryJarFileDisplay = new JLabel("");
		add(dictionaryJarFileDisplay, BorderLayout.NORTH);

		add(new JLabel("Package"), BorderLayout.WEST);

		var sourcePackageEditor = new JTextField();
		add(sourcePackageEditor, BorderLayout.CENTER);

		var importButton = new JButton("Import");
		add(importButton, BorderLayout.EAST);
		importButton.addActionListener(SwingHelper.action(LambdaExt.uncheck(() -> {
			importDictionary(new File(dictionaryJarFileDisplay.getText()), sourcePackageEditor.getText());
			imported = true;
			setVisible(false);
		}, e -> TCWriterGui.handleException(this, e))));
	}

	public boolean runImport() {
		pack();
		var chooser = new JFileChooser();
		chooser.setDialogTitle("Dictionary import");
		chooser.setFileFilter(new FileFilter() {

			@Override
			public String getDescription() {
				return "Dictionary file (Fat Jar)";
			}

			@Override
			public boolean accept(File f) {
				return f.isDirectory() || f.getName().endsWith(".jar");
			}
		});
		int result = chooser.showOpenDialog(this);
		if (result == JFileChooser.APPROVE_OPTION) {
			dictionaryJarFileDisplay.setText(chooser.getSelectedFile().toString());
			setModal(true);
			pack();
			setLocationRelativeTo(parentFrame);
			setVisible(true);
		} else {
			System.exit(1);
		}
		return imported;
	}

	protected void importDictionary(File dictionaryJarFile, String sourcePackage) {
		try (URLClassFinder finder = ClassFinder.source(dictionaryJarFile)) {
			var dictionary = finder.withAnnotation(TCRole.class, Policy.CLASS_ONLY)
					.withAnnotation(TCActors.class, Policy.CLASS_ONLY).withPackages(sourcePackage).scan()
					.collect(JavaToDictionary.toDictionary());
			modelDao.writeTestDictionary(dictionary);
		} catch (IOException e) {
			TCWriterGui.handleException(this, e);
		}
	}

}
