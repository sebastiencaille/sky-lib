package ch.scaille.gui.swing.tools;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Window;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.*;

import ch.scaille.gui.mvc.properties.ErrorSet;
import ch.scaille.gui.tools.GenericEditorController;
import ch.scaille.gui.tools.IGenericEditor;

public class SwingGenericEditorDialog extends JDialog {

	private JTabbedPane tabbedPane = null;
	private List<GenericEditorController<?>> controllers = new ArrayList<>();

	public SwingGenericEditorDialog(final Window parent, final String title, final ModalityType modality) {
		super(parent, title, modality);
		getContentPane().setLayout(new BorderLayout());
	}

	public IGenericEditor mainPanel() {
		final SwingGenericEditorPanel panel = new SwingGenericEditorPanel();
	getContentPane().add(panel, BorderLayout.CENTER);
		return panel;
	}

	public IGenericEditor tab(String name) {
		if (tabbedPane == null) {
			tabbedPane = new JTabbedPane(SwingConstants.TOP);
			getContentPane().add(tabbedPane, BorderLayout.CENTER);
		}
		final SwingGenericEditorPanel panel = new SwingGenericEditorPanel();
		tabbedPane.add(name, panel);
		return panel;
	}

	public void add(GenericEditorController<?> controller) {
		controllers.add(controller);
	}

	public void build(final ErrorSet errorProperty) {
		final JPanel buttonPanel = new JPanel(new FlowLayout());
		final JButton okButton = new JButton("OK");
		errorProperty.getErrors().bind(Map::isEmpty).listen(okButton::setEnabled);
		buttonPanel.add(okButton);
		okButton.addActionListener(e -> {
			controllers.forEach(GenericEditorController::save);
			close();
		});

		final JButton cancelButton = new JButton("Cancel");
		buttonPanel.add(cancelButton);
		getContentPane().add(buttonPanel, BorderLayout.SOUTH);
		cancelButton.addActionListener(e -> this.close());

		validate();
		pack();
		setSize((int) (getWidth() * 1.2), (int) (getHeight() * 1.2));
	}

	private void close() {
		setVisible(false);
		dispose();
	}

}
