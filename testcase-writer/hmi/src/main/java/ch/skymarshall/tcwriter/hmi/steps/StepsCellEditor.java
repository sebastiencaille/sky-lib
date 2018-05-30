package ch.skymarshall.tcwriter.hmi.steps;

import static ch.skymarshall.tcwriter.generators.Helper.toReference;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.skymarshall.hmi.swing.model.ListModelTableModel;

import ch.skymarshall.tcwriter.generators.Helper.Reference;
import ch.skymarshall.tcwriter.generators.model.IdObject;
import ch.skymarshall.tcwriter.generators.model.TestCase;
import ch.skymarshall.tcwriter.generators.model.TestParameter.ParameterNature;
import ch.skymarshall.tcwriter.generators.model.TestParameterType;
import ch.skymarshall.tcwriter.generators.model.TestStep;
import ch.skymarshall.tcwriter.hmi.steps.StepsTableModel.Column;

public class StepsCellEditor extends DefaultCellEditor {
	private final TestCase tc;

	public StepsCellEditor(final TestCase tc) {
		super(new JComboBox<>());
		this.tc = tc;
	}

	@Override
	public Component getTableCellEditorComponent(final JTable table, final Object value, final boolean isSelected,
			final int row, final int column) {
		final TestStep step = ((ListModelTableModel<TestStep, ?>) table.getModel()).getObjectAtRow(row);
		final Column columnEnum = Column.valueOf(table.getColumnName(column));

		Collection<? extends IdObject> values;
		switch (columnEnum) {
		case ACTOR:
			values = tc.getModel().getActors().values();
			break;
		case METHOD:
			values = step.getRole().getApis();
			break;
		case PARAM0:
			editorComponent = getParamEditor(table, step, 0, (Reference) value);
			return editorComponent;
		case PARAM1:
			editorComponent = getParamEditor(table, step, 1, (Reference) value);
			return editorComponent;
		case TO_VALUE:
			values = tc.getReferences(step.getAction().getReturnType());
			break;
		default:
			return new JTextField((String) value);
		}

		final JComponent cb = prepareFastListEditor(value,
				toReference(tc.getModel(), values, ParameterNature.TEST_API_TYPE));
		editorComponent = cb;
		return editorComponent;
	}

	private JComponent getParamEditor(final JTable table, final TestStep step, final int index, final Reference value) {

		final TestParameterType parameter = step.getAction().getParameter(index);
		final List<Reference> refsReferences = toReference(tc.getModel(), tc.getReferences(parameter.getType()),
				ParameterNature.REFERENCE);

		if (!parameter.isSimpleType()) {
			final List<Reference> apiReferences = toReference(tc.getModel(),
					tc.getModel().getParameterFactories().get(parameter.getType()), ParameterNature.TEST_API_TYPE);
			return prepareFastListEditor(value, refsReferences, apiReferences);
		}

		final JComboBox<Reference> fastRefEditor = prepareFastListEditor(value, refsReferences);
		final JDialog dialog = new JDialog();
		dialog.getContentPane().setLayout(new BoxLayout(dialog.getContentPane(), BoxLayout.Y_AXIS));
		final JTextField freeTyping = new JTextField(value.getDisplay());
		dialog.getContentPane().add(freeTyping);

		dialog.getContentPane().add(fastRefEditor);
		fastRefEditor.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(final ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					freeTyping.setText("");
				}
			}
		});
		final JButton ok = new JButton("OK");
		delegate = new EditorDelegate() {

			@Override
			public Object getCellEditorValue() {
				if (freeTyping.getText().isEmpty()) {
					return fastRefEditor.getSelectedItem();
				}
				return new Reference("", freeTyping.getText(), ParameterNature.SIMPLE_TYPE);
			}

			@Override
			public void actionPerformed(final ActionEvent e) {
				super.actionPerformed(e);
				dialog.setVisible(false);
				dialog.dispose();
			}

		};
		ok.addActionListener(delegate);
		SwingUtilities.invokeLater(() -> {
			dialog.add(ok);
			dialog.pack();
			dialog.setModal(true);
			dialog.setVisible(true);
		});
		return new JPanel();
	}

	private JComboBox<Reference> prepareFastListEditor(final Object value, final List<Reference>... references) {
		final JComboBox<Reference> cb = new JComboBox<>(Arrays.stream(references).flatMap(a -> a.stream())
				.collect(Collectors.toList()).toArray(new Reference[0]));
		cb.setSelectedItem(value);
		return cb;
	}

}
