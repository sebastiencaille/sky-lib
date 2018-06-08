package ch.skymarshall.tcwriter.hmi.editors;

import static ch.skymarshall.tcwriter.hmi.steps.StepsCellEditor.prepareFastListEditor;

import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import ch.skymarshall.tcwriter.generators.Helper.Reference;
import ch.skymarshall.tcwriter.generators.model.TestParameter.ParameterNature;

public class ReferenceEditor extends JDialog {

	private final JTextField freeTyping;
	private final JComboBox<Reference> fastRefEditor;
	private final JButton ok;

	public ReferenceEditor(final List<Reference> refsReferences, final Reference value) {

		fastRefEditor = prepareFastListEditor(value, refsReferences);
		final JDialog dialog = new JDialog();
		dialog.getContentPane().setLayout(new BoxLayout(dialog.getContentPane(), BoxLayout.Y_AXIS));
		freeTyping = new JTextField();
		if (value.getNature() == ParameterNature.SIMPLE_TYPE) {
			freeTyping.setText(value.getDisplay());
		}
		dialog.getContentPane().add(freeTyping);

		dialog.getContentPane().add(fastRefEditor);
		fastRefEditor.addItemListener(e -> {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				freeTyping.setText("");
			}
		});
		ok = new JButton("OK");

		SwingUtilities.invokeLater(() -> {
			dialog.add(ok);
			dialog.pack();
			dialog.setModal(true);
			dialog.setVisible(true);
		});
	}

	public Reference getValue() {
		if (freeTyping.getText().isEmpty()) {
			return (Reference) fastRefEditor.getSelectedItem();
		}
		return new Reference("", freeTyping.getText(), ParameterNature.SIMPLE_TYPE);
	}

	public void close() {
		setVisible(false);
		dispose();
	}

	public void setOkAction(final ActionListener delegate) {
		ok.addActionListener(delegate);
	}
}
