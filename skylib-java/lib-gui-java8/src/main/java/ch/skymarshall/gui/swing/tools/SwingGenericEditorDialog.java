package ch.skymarshall.gui.swing.tools;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Window;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

import ch.skymarshall.gui.mvc.IBindingController;
import ch.skymarshall.gui.tools.GenericEditorClassModel.PropertyEntry;
import ch.skymarshall.gui.tools.GenericEditorAdapter;
import ch.skymarshall.gui.tools.IGenericEditor;

public class SwingGenericEditorDialog extends JDialog implements IGenericEditor {

	private final SwingGenericEditorPanel panel;

	public SwingGenericEditorDialog(final Window parent, final String title, final ModalityType modality) {
		super(parent, title, modality);
		getContentPane().setLayout(new BorderLayout());
		panel = new SwingGenericEditorPanel();
	}

	@Override
	public IBindingController bind(final PropertyEntry<?> prop) {
		return panel.bind(prop);
	}

	@Override
	public void finish(final GenericEditorAdapter<?, ?> adapter) {
		add(panel, BorderLayout.CENTER);

		final JPanel buttonPanel = new JPanel(new FlowLayout());
		final JButton okButton = new JButton("OK");
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
	}

	private void close() {
		setVisible(false);
		dispose();
	}

}
