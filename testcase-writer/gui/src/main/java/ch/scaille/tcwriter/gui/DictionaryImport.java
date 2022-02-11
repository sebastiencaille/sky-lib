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

import ch.scaille.tcwriter.annotations.TCActors;
import ch.scaille.tcwriter.annotations.TCRole;
import ch.scaille.tcwriter.generators.JavaToDictionary;
import ch.scaille.tcwriter.generators.model.persistence.IModelPersister;
import ch.scaille.tcwriter.generators.model.testapi.TestDictionary;
import ch.scaille.tcwriter.gui.frame.TCWriterGui;
import ch.scaille.util.helpers.ClassFinder;
import ch.scaille.util.helpers.ClassFinder.Policy;
import ch.scaille.util.helpers.LambdaExt;

public class DictionaryImport extends JDialog {

	private final IModelPersister persister;
	private final Component parentFrame;
	private final JLabel dictionaryJarFile;
	private boolean imported = true;

	public DictionaryImport(Component parentFrame, IModelPersister persister) {
		this.parentFrame = parentFrame;
		this.persister = persister;
		setLayout(new BorderLayout());

		dictionaryJarFile = new JLabel("");
		add(dictionaryJarFile, BorderLayout.NORTH);

		add(new JLabel("Package"), BorderLayout.WEST);

		JTextField sourcePackage = new JTextField();
		add(sourcePackage, BorderLayout.CENTER);

		JButton importButton = new JButton("Import");
		add(importButton, BorderLayout.EAST);
		importButton.addActionListener(a -> LambdaExt.uncheck(() -> {
			importDictionary(new File(dictionaryJarFile.getText()), sourcePackage.getText());
			imported = true;
			setVisible(false);
		}, e -> TCWriterGui.handleException(this, e)));
	}

	public boolean runImport() {
		pack();
		JFileChooser chooser = new JFileChooser();
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
			dictionaryJarFile.setText(chooser.getSelectedFile().toString());
			setModal(true);
			pack();
			setLocationRelativeTo(parentFrame);
			setVisible(true);
		} else {
			setVisible(false);
		}
		return imported;
	}

	protected void importDictionary(File dictionaryJarFile, String sourcePackage) {
		try {
			TestDictionary dictionary = ClassFinder.source(dictionaryJarFile)
					.withAnnotation(TCRole.class, Policy.CLASS_ONLY).withAnnotation(TCActors.class, Policy.CLASS_ONLY)
					.withPackages(sourcePackage).scan().collect(JavaToDictionary.toDictionary());
			persister.writeTestDictionary(dictionary);
		} catch (IOException e) {
			TCWriterGui.handleException(this, e);
		}
	}

}
