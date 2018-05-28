package ch.skymarshall.tcwriter.hmi.steps;

import static ch.skymarshall.tcwriter.generators.Helper.toReference;

import java.awt.Component;
import java.util.Collection;
import java.util.Vector;

import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JTable;

import org.skymarshall.hmi.swing.model.ListModelTableModel;

import ch.skymarshall.tcwriter.generators.Helper.Reference;
import ch.skymarshall.tcwriter.generators.model.IdObject;
import ch.skymarshall.tcwriter.generators.model.TestCase;
import ch.skymarshall.tcwriter.generators.model.TestParameter;
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

		DefaultComboBoxModel<Reference> cbModel;
		switch (columnEnum) {
		case ACTOR:
			cbModel = comboBoxOf(tc.getModel().getActors().values());
			break;
		case METHOD:
			cbModel = comboBoxOf(step.getRole().getApis());
			break;
		case PARAM0:
			cbModel = comboBoxOf(getTestParameterForType(step, 0));
			break;
		case PARAM1:
			cbModel = comboBoxOf(getTestParameterForType(step, 1));
			break;
		default:
			cbModel = new DefaultComboBoxModel<>();
		}
		final JComboBox<Reference> cb = (JComboBox<Reference>) editorComponent;
		cb.setModel(cbModel);

		return editorComponent;
	}

	private Collection<TestParameter> getTestParameterForType(final TestStep step, final int paramIndex) {
		return tc.getModel().getParameterFactories().get(step.getAction().getParameters().get(paramIndex).getType());
	}

	private DefaultComboBoxModel<Reference> comboBoxOf(final Collection<? extends IdObject> tcObjects) {
		return new DefaultComboBoxModel<>(new Vector<>(toReference(tc.getModel(), tcObjects)));
	}
}
