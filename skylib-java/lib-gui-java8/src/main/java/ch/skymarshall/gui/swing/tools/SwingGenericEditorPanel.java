package ch.skymarshall.gui.swing.tools;

import static ch.skymarshall.gui.swing.bindings.SwingBindings.selected;
import static ch.skymarshall.gui.swing.bindings.SwingBindings.value;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Arrays;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;

import ch.skymarshall.gui.mvc.IBindingController;
import ch.skymarshall.gui.tools.GenericEditorAdapter;
import ch.skymarshall.gui.tools.GenericEditorClassModel.PropertyEntry;
import ch.skymarshall.gui.tools.IGenericEditor;

public class SwingGenericEditorPanel extends JPanel implements IGenericEditor {

	private int currentRow;

	public SwingGenericEditorPanel() {
		setLayout(new GridBagLayout());
	}

	@Override
	public IBindingController bind(final PropertyEntry<?> prop) {

		final Class<?> propType = prop.getPropertyType();
		if (propType == Boolean.class) {
			final JCheckBox cb = new JCheckBox(prop.getLabel());
			final GridBagConstraints cbConstraint = new GridBagConstraints();
			cbConstraint.gridx = 1;
			cbConstraint.gridwidth = 2;
			cbConstraint.gridy = ++currentRow;
			cbConstraint.anchor = GridBagConstraints.WEST;
			cbConstraint.insets = new Insets(5, 5, 0, 5);
			add(cb, cbConstraint);
			return prop.getProperty(Boolean.class).bind(selected(cb));
		} else if (propType == Integer.class) {
			currentRow++;
			addLabel(prop);
			return prop.getProperty(Integer.class).bind(value(addSpinner(prop)));
		} else if (propType == Long.class) {
			currentRow++;
			addLabel(prop);
			return prop.getProperty(Long.class).bind(value(addSpinner(prop)));
		} else if (propType == String.class) {
			currentRow++;
			addLabel(prop);
			return prop.getProperty(String.class).bind(value(addTextField(prop)));
		}
		throw new IllegalStateException("Type not handled: " + prop.getPropertyType());
	}

	private void addLabel(final PropertyEntry<?> prop) {
		final JLabel label = new JLabel(prop.getLabel());
		label.setToolTipText(prop.getTooltip());
		final GridBagConstraints labelConstraint = new GridBagConstraints();
		labelConstraint.gridx = 1;
		labelConstraint.gridy = currentRow;
		labelConstraint.insets = new Insets(5, 5, 0, 5);
		labelConstraint.anchor = GridBagConstraints.EAST;
		add(label, labelConstraint);
	}

	private JSpinner addSpinner(final PropertyEntry<?> prop) {
		final JSpinner sp = new JSpinner();
		sp.setToolTipText(prop.getTooltip());

		JComponent displayed = sp;
		if (prop.isReadOnly()) {
			displayed = sp.getEditor();
			Arrays.stream(displayed.getComponents()).filter(c -> c instanceof JTextField)
					.forEach(c -> ((JTextField) c).setEditable(false));
			displayed.setBorder(sp.getBorder());
		}

		final GridBagConstraints fieldConstraint = new GridBagConstraints();
		fieldConstraint.fill = GridBagConstraints.HORIZONTAL;
		fieldConstraint.gridx = 2;
		fieldConstraint.weightx = 1.0;
		fieldConstraint.gridy = currentRow;
		fieldConstraint.insets = new Insets(5, 0, 0, 5);
		add(displayed, fieldConstraint);
		return sp;
	}

	private JTextField addTextField(final PropertyEntry<?> prop) {
		final JTextField tf = new JTextField();
		tf.setToolTipText(prop.getTooltip());
		tf.setEditable(!prop.isReadOnly());
		final GridBagConstraints fieldConstraint = new GridBagConstraints();
		fieldConstraint.fill = GridBagConstraints.HORIZONTAL;
		fieldConstraint.gridx = 2;
		fieldConstraint.weightx = 1.0;
		fieldConstraint.gridy = currentRow;
		fieldConstraint.insets = new Insets(5, 0, 0, 5);
		add(tf, fieldConstraint);
		return tf;
	}

	@Override
	public void finish(final GenericEditorAdapter<?, ?> adapter) {
		// nope
	}
}
