package ch.skymarshall.tcwriter.hmi.editors;

import static ch.skymarshall.tcwriter.hmi.steps.StepsCellEditor.prepareFastListEditor;

import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import ch.skymarshall.tcwriter.generators.Helper.VerbatimValue;
import ch.skymarshall.tcwriter.generators.model.testapi.TestParameterDefinition.ParameterNature;

public class VerbatimValueEditor extends JDialog {

	private final JTextField freeTyping;
	private final JComboBox<VerbatimValue> fastRefEditor;
	private final JButton ok;

	public VerbatimValueEditor(final List<VerbatimValue> refsReferences, final VerbatimValue simpleValue) {

		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		freeTyping = new JTextField();
		if (simpleValue.getNature() == ParameterNature.SIMPLE_TYPE) {
			freeTyping.setText(simpleValue.getDisplay());
		}
		getContentPane().add(freeTyping);

		fastRefEditor = prepareFastListEditor(refsReferences);
		fastRefEditor.setSelectedItem(simpleValue);
		getContentPane().add(fastRefEditor);
		fastRefEditor.addItemListener(e -> {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				freeTyping.setText("");
			}
		});
		ok = new JButton("OK");
		add(ok);

		setMinimumSize(new Dimension(300, 100));
		pack();
		setModal(true);

		SwingUtilities.invokeLater(() -> setVisible(true));
	}

	public VerbatimValue getValue() {
		if (freeTyping.getText().isEmpty()) {
			return (VerbatimValue) fastRefEditor.getSelectedItem();
		}
		return new VerbatimValue("", freeTyping.getText(), ParameterNature.SIMPLE_TYPE);
	}

	public void close() {
		setVisible(false);
		dispose();
	}

	public void setOkAction(final ActionListener delegate) {
		ok.addActionListener(delegate);
	}
}
