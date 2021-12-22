package ch.scaille.gui.swing.tools;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Window;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

import ch.scaille.gui.mvc.IBindingController;
import ch.scaille.gui.mvc.properties.ErrorSet;
import ch.scaille.gui.tools.GenericEditorController;
import ch.scaille.gui.tools.IGenericEditor;
import ch.scaille.gui.tools.PropertyEntry;

public class SwingGenericEditorDialog extends JDialog implements IGenericEditor {

	private final SwingGenericEditorPanel panel;

	public SwingGenericEditorDialog(final Window parent, final String title, final ModalityType modality) {
		super(parent, title, modality);
		getContentPane().setLayout(new BorderLayout());
		panel = new SwingGenericEditorPanel();
	}

	@Override
	public void build(final GenericEditorController<?> adapter, final ErrorSet errorProperty) {
		panel.build(adapter, errorProperty);
		add(panel, BorderLayout.CENTER);

		final JPanel buttonPanel = new JPanel(new FlowLayout());
		final JButton okButton = new JButton("OK");
		errorProperty.getErrors().bind(Map::isEmpty).listen(okButton::setEnabled);
		buttonPanel.add(okButton);
		okButton.addActionListener(e -> {
			adapter.save();
			close();
		});

		final JButton cancelButton = new JButton("Cancel");
		buttonPanel.add(cancelButton);
		add(buttonPanel, BorderLayout.SOUTH);
		cancelButton.addActionListener(e -> this.close());

		validate();
		pack();
		setSize((int) (getWidth() * 1.2), (int) (getHeight() * 1.2));
	}

	@Override
	public IBindingController addEntry(final PropertyEntry prop, final ErrorSet errorProperty) {
		return panel.addEntry(prop, errorProperty);
	}

	private void close() {
		setVisible(false);
		dispose();
	}

}
