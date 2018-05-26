package ch.skymarshall.tcwriter.hmi.steps;

import static ch.skymarshall.tcwriter.generators.Helper.toDescription;

import java.awt.Component;
import java.util.Collection;
import java.util.Vector;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;

import org.skymarshall.hmi.swing.model.ListModelTableModel;

import ch.skymarshall.tcwriter.generators.model.TestCase;
import ch.skymarshall.tcwriter.generators.model.TestObject;
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
		switch (columnEnum) {
		case ACTOR:
			return new JComboBox<>(new Vector<>(toDescription(tc.getModel(), tc.getModel().getActors().values())));
		case METHOD:
			return new JComboBox<>(new Vector<>(toDescription(tc.getModel(), step.getActor().getApis())));
		case SELECTOR:
			final Collection<TestObject> testObjects = tc.getModel().getTestObjects()
					.get(step.getMethod().getParameters().get(0).getType());
			return new JComboBox<>(new Vector<>(toDescription(tc.getModel(), testObjects)));
		}
		return null;
	}
}
