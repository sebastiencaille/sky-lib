package ch.scaille.gui.swing.tools;

import static ch.scaille.gui.swing.factories.SwingBindings.selected;
import static ch.scaille.gui.swing.factories.SwingBindings.value;
import static ch.scaille.javabeans.Converters.guiErrorToString;
import static ch.scaille.javabeans.Converters.listen;
import static ch.scaille.javabeans.Converters.mapContains;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
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

import ch.scaille.gui.tools.GenericEditorController;
import ch.scaille.gui.tools.IGenericEditor;
import ch.scaille.gui.tools.PropertyEntry;
import ch.scaille.javabeans.IBindingController;
import ch.scaille.javabeans.properties.ErrorSet;

public class SwingGenericEditorPanel extends JPanel implements IGenericEditor {

	private int currentRow;

	public SwingGenericEditorPanel() {
		setLayout(new GridBagLayout());
	}

	@Override
	public IBindingController addEntry(final PropertyEntry prop, ErrorSet errors) {

		IBindingController result = null;

		final var propType = prop.getEndOfChainType();
		if (propType == Boolean.class) {
			final var cb = new JCheckBox(prop.getLabel());
			final var cbConstraint = new GridBagConstraints();
			cbConstraint.gridx = 1;
			cbConstraint.gridwidth = 2;
			cbConstraint.gridy = ++currentRow;
			cbConstraint.anchor = GridBagConstraints.WEST;
			cbConstraint.insets = new Insets(5, 5, 0, 5);
			add(cb, cbConstraint);
			result = prop.getChain(Boolean.class).bind(selected(cb));
		} else if (propType == Integer.class) {
			currentRow++;
			addLabel(prop);
			final var component = addSpinner(prop);
			result = prop.getChain(Integer.class).bind(value(component));
		} else if (propType == Long.class) {
			currentRow++;
			addLabel(prop);
			final var component = addSpinner(prop);
			result = prop.getChain(Long.class).bind(value(component));
		} else if (propType == String.class) {
			currentRow++;
			addLabel(prop);
			final var component = addTextField(prop);
			result = prop.getChain(String.class).bind(value(component));
		}
		addErrorDisplay(errors, prop);
		if (result == null) {
			throw new IllegalStateException("Type not handled: " + prop.getEndOfChainType());
		}
		return result;
	}

	protected JLabel addLabel(final PropertyEntry prop) {
		final var labelConstraint = new GridBagConstraints();
		final var label = addLabel(labelConstraint);
		label.setText(prop.getLabel());
		label.setToolTipText(prop.getTooltip());
		return label;
	}

	protected JLabel addLabel(final GridBagConstraints labelConstraint) {
		final var label = new JLabel();
		labelConstraint.gridx = 1;
		labelConstraint.gridy = currentRow;
		labelConstraint.insets = new Insets(5, 5, 0, 5);
		labelConstraint.anchor = GridBagConstraints.EAST;
		add(label, labelConstraint);
		return label;
	}

	protected JSpinner addSpinner(final PropertyEntry prop) {
		final var spinner = new JSpinner();
		spinner.setToolTipText(prop.getTooltip());

		JComponent displayed = spinner;
		if (prop.isReadOnly()) {
			displayed = spinner.getEditor();
			Arrays.stream(displayed.getComponents())
					.filter(JTextField.class::isInstance)
					.map(JTextField.class::cast)
					.forEach(c -> c.setEditable(false));
			displayed.setBorder(spinner.getBorder());
		}

		final var fieldConstraint = new GridBagConstraints();
		fieldConstraint.fill = GridBagConstraints.HORIZONTAL;
		fieldConstraint.gridx = 2;
		fieldConstraint.weightx = 1.0;
		fieldConstraint.gridy = currentRow;
		fieldConstraint.insets = new Insets(5, 0, 0, 5);
		add(displayed, fieldConstraint);
		return spinner;
	}

	protected JTextField addTextField(final PropertyEntry prop) {
		final var tf = new JTextField();
		tf.setToolTipText(prop.getTooltip());
		tf.setEditable(!prop.isReadOnly());

		final var fieldConstraint = new GridBagConstraints();
		fieldConstraint.fill = GridBagConstraints.HORIZONTAL;
		fieldConstraint.gridx = 2;
		fieldConstraint.weightx = 1.0;
		fieldConstraint.gridy = currentRow;
		fieldConstraint.insets = new Insets(5, 0, 0, 5);
		add(tf, fieldConstraint);
		return tf;
	}

	protected void addErrorDisplay(final ErrorSet errorProperty, final PropertyEntry prop) {
		var errorLabel = new JLabel("");
		errorLabel.setPreferredSize(new Dimension(20, 20));
		final var fieldConstraint = new GridBagConstraints();
		fieldConstraint.gridx = 3;
		fieldConstraint.weightx = 0.0;
		fieldConstraint.gridy = currentRow;
		fieldConstraint.insets = new Insets(1, 0, 0, 1);
		add(errorLabel, fieldConstraint);

		errorLabel.setForeground(Color.RED);
		errorLabel.setFont(errorLabel.getFont().deriveFont(Font.BOLD));
		errorProperty.getErrors()
				.bind(listen(m -> m.get(prop.getProperty())))
				.bind(guiErrorToString())
				.listen(errorLabel::setToolTipText);
		errorProperty.getErrors().bind(mapContains(prop.getProperty(), "*", "")).listen(errorLabel::setText);
	}

	@Override
	public void build(final GenericEditorController<?> adapter, final ErrorSet errorProperty) {
		// default
	}
}
