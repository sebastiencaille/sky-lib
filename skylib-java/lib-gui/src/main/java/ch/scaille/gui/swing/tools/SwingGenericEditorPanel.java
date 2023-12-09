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
import javax.swing.text.JTextComponent;

import ch.scaille.gui.tools.GenericEditorController;
import ch.scaille.gui.tools.IGenericEditor;
import ch.scaille.gui.tools.PropertyEntry;
import ch.scaille.javabeans.IBindingController;
import ch.scaille.javabeans.properties.ErrorSet;

/**
 * Swing based editor panel
 */
public class SwingGenericEditorPanel extends JPanel implements IGenericEditor {

	private int currentRow;

	public SwingGenericEditorPanel() {
		setLayout(new GridBagLayout());
	}

	@Override
	public IBindingController addEntry(final PropertyEntry prop, ErrorSet errors) {

		IBindingController result = null;

		final var propType = prop.getEndOfChainType();
		currentRow++;
		if (propType == Boolean.class) {
			final var cb = addBooleanComponent(prop);
			result = prop.getChain(Boolean.class).bind(selected(cb));
		} else if (propType == Integer.class) {
			addLabel(prop);
			final var component = addNumberComponent(prop);
			result = prop.getChain(Integer.class).bind(value(component));
		} else if (propType == Long.class) {
			addLabel(prop);
			final var component = addNumberComponent(prop);
			result = prop.getChain(Long.class).bind(value(component));
		} else if (propType == String.class) {
			addLabel(prop);
			final var component = addStringComponent(prop);
			result = prop.getChain(String.class).bind(value(component));
		}
		addErrorDisplay(errors, prop);
		if (result == null) {
			throw new IllegalStateException("Type not handled: " + prop.getEndOfChainType());
		}
		return result;
	}

	protected <C extends JComponent> C setup(C component, PropertyEntry prop) {
		component.setToolTipText(prop.getTooltip());
		if (component instanceof JTextComponent) {
			((JTextComponent)component).setEditable(!prop.isReadOnly());
		}
		return component;
	}
	
	protected JCheckBox addBooleanComponent(final PropertyEntry prop) {
		final var cb = new JCheckBox(prop.getLabel());
		final var cbConstraint = new GridBagConstraints();
		cbConstraint.gridx = 1;
		cbConstraint.gridwidth = 2;
		cbConstraint.gridy = currentRow;
		cbConstraint.anchor = GridBagConstraints.WEST;
		cbConstraint.insets = new Insets(5, 5, 0, 5);
		add(cb, cbConstraint);
		return cb;
	}
	
	protected JLabel addLabel(final PropertyEntry prop) {
		final var label = new JLabel();
		label.setText(prop.getLabel());
		
		final var labelConstraint = new GridBagConstraints();
		labelConstraint.gridx = 1;
		labelConstraint.gridy = currentRow;
		labelConstraint.insets = new Insets(5, 5, 0, 5);
		labelConstraint.anchor = GridBagConstraints.EAST;
		add(setup(label, prop), labelConstraint);
		return label;
	}

	protected JSpinner addNumberComponent(final PropertyEntry prop) {
		final var spinner = new JSpinner();

		JComponent displayed = spinner;
		if (prop.isReadOnly()) {
			displayed = spinner.getEditor();
			Arrays.stream(displayed.getComponents())
					.filter(JComponent.class::isInstance)
					.forEach(c -> setup((JComponent)c, prop));
			displayed.setBorder(spinner.getBorder());
		}

		add(setup(displayed, prop), defaultEditorGridBagConstraints(currentRow));
		return spinner;
	}

	protected JTextField addStringComponent(final PropertyEntry prop) {
		final var textField = new JTextField();
		add(setup(textField, prop), defaultEditorGridBagConstraints(currentRow));
		return textField;
	}
	
	protected GridBagConstraints defaultEditorGridBagConstraints(int row) {
		final var fieldConstraint = new GridBagConstraints();
		fieldConstraint.fill = GridBagConstraints.HORIZONTAL;
		fieldConstraint.gridx = 2;
		fieldConstraint.weightx = 1.0;
		fieldConstraint.gridy = row;
		fieldConstraint.insets = new Insets(5, 0, 0, 5);
		return fieldConstraint;
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
