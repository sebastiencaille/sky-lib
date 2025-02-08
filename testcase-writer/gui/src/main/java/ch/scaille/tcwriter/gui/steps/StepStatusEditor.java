package ch.scaille.tcwriter.gui.steps;

import java.awt.Component;

import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JTable;

import ch.scaille.tcwriter.model.testexec.StepStatus;

public class StepStatusEditor extends DefaultCellEditor {

	private static final long serialVersionUID = -4623973824541421254L;

	public StepStatusEditor() {
		super(new JCheckBox());
	}

	@Override
	public Component getTableCellEditorComponent(final JTable table, final Object value, final boolean hasFocus,
			final int row, final int column) {
		final var status = (StepStatus) value;
		final var component = (JCheckBox) getComponent();
		component.setSelected(status.isBreakPoint());
		return component;
	}

}
