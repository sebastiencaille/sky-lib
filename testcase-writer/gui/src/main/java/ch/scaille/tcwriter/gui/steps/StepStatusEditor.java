package ch.scaille.tcwriter.gui.steps;

import java.awt.Component;

import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JTable;

import ch.scaille.tcwriter.stepping.StepStatus;

public class StepStatusEditor extends DefaultCellEditor {

	public StepStatusEditor() {
		super(new JCheckBox());
	}

	@Override
	public Component getTableCellEditorComponent(final JTable table, final Object value, final boolean hasFocus,
			final int row, final int column) {
		final StepStatus status = (StepStatus) value;
		((JCheckBox) getComponent()).setSelected(status.breakPoint);
		return getComponent();
	}

}
