package ch.skymarshall.gui.swing.tools;

import static ch.skymarshall.gui.mvc.converters.Converters.intToString;
import static ch.skymarshall.gui.swing.bindings.SwingBindings.selected;
import static ch.skymarshall.gui.swing.bindings.SwingBindings.value;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import ch.skymarshall.gui.mvc.IBindingController;
import ch.skymarshall.gui.tools.ClassAdapter;
import ch.skymarshall.gui.tools.GenericModelEditorAdapter;
import ch.skymarshall.gui.tools.IGenericEditor;
import ch.skymarshall.gui.tools.ClassAdapter.PropertyEntry;

public class SwingGenericEditorPanel extends JPanel implements IGenericEditor {

	private int currentRow;

	public SwingGenericEditorPanel() {
		setLayout(new GridBagLayout());
	}

	@Override
	public IBindingController bind(final PropertyEntry prop) {

		final Class<?> propType = prop.getPropertyType();
		if (propType == Boolean.TYPE || propType == Boolean.class) {
			final JCheckBox cb = new JCheckBox(prop.getLabel());
			final GridBagConstraints labelConstraint = new GridBagConstraints();
			labelConstraint.gridx = 1;
			labelConstraint.gridwidth = 2;
			labelConstraint.gridy = ++currentRow;
			add(cb, labelConstraint);
			return prop.getProperty(Boolean.class).bind(selected(cb));
		} else if (propType == Integer.TYPE || propType == Integer.class) {
			currentRow++;
			addLabel(prop);
			return prop.getProperty(Integer.class).bind(intToString()).bind(value(addTextField()));
		} else if (propType == String.class) {
			currentRow++;
			addLabel(prop);
			return prop.getProperty(String.class).bind(value(addTextField()));
		}
		throw new IllegalStateException("Type not handled: " + prop.getPropertyType());
	}

	private void addLabel(final PropertyEntry<?> prop) {
		final JLabel label = new JLabel(prop.getLabel());
		final GridBagConstraints labelConstraint = new GridBagConstraints();
		labelConstraint.gridx = 1;
		labelConstraint.gridy = currentRow;
		add(label, labelConstraint);
	}

	private JTextField addTextField() {
		final JTextField tf = new JTextField();
		final GridBagConstraints labelConstraint = new GridBagConstraints();
		labelConstraint.gridx = 2;
		labelConstraint.fill = GridBagConstraints.HORIZONTAL;
		labelConstraint.gridy = currentRow;
		add(tf, labelConstraint);
		return tf;
	}

	@Override
	public void finish(final GenericModelEditorAdapter<?, ?> adapter) {
		// nope
	}
}
