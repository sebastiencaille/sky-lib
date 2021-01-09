package ch.skymarshall.gui.swing.tools;

import static ch.skymarshall.gui.swing.factories.SwingBindings.selected;
import static ch.skymarshall.gui.swing.factories.SwingBindings.value;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;

import ch.skymarshall.gui.mvc.IBindingController;
import ch.skymarshall.gui.mvc.properties.ErrorSet;
import ch.skymarshall.gui.tools.GenericEditorController;
import ch.skymarshall.gui.tools.IGenericEditor;
import ch.skymarshall.gui.tools.PropertyEntry;

public class SwingGenericEditorPanel extends JPanel implements IGenericEditor {

	private int currentRow;

	private static class ErrorHandler {
		final JComponent errorComponent;
		final Color backup;

		public ErrorHandler(final JComponent errorComponent) {
			this.errorComponent = errorComponent;
			this.backup = errorComponent.getForeground();
		}

		public void setError() {
			errorComponent.setForeground(Color.RED.darker());
		}

		public void unsetError() {
			errorComponent.setForeground(backup);
		}
	}

	private final Map<JComponent, ErrorHandler> errorHandlers = new HashMap<>();

	public SwingGenericEditorPanel() {
		setLayout(new GridBagLayout());
	}

	@Override
	public IBindingController addEntry(final PropertyEntry prop) {

		final Class<?> propType = prop.getEndOfChainType();
		if (propType == Boolean.class) {
			final JCheckBox cb = new JCheckBox(prop.getLabel());
			final GridBagConstraints cbConstraint = new GridBagConstraints();
			cbConstraint.gridx = 1;
			cbConstraint.gridwidth = 2;
			cbConstraint.gridy = ++currentRow;
			cbConstraint.anchor = GridBagConstraints.WEST;
			cbConstraint.insets = new Insets(5, 5, 0, 5);
			add(cb, cbConstraint);
			errorHandlers.put(cb, new ErrorHandler(cb));
			return prop.getChain(Boolean.class).bind(selected(cb));
		} else if (propType == Integer.class) {
			currentRow++;
			final JLabel label = addLabel(prop);
			final JSpinner component = addSpinner(prop);
			errorHandlers.put(component, new ErrorHandler(label));
			return prop.getChain(Integer.class).bind(value(component));
		} else if (propType == Long.class) {
			currentRow++;
			final JLabel label = addLabel(prop);
			final JSpinner component = addSpinner(prop);
			errorHandlers.put(component, new ErrorHandler(label));
			return prop.getChain(Long.class).bind(value(component));
		} else if (propType == String.class) {
			currentRow++;
			final JLabel label = addLabel(prop);
			final JTextField component = addTextField(prop);
			errorHandlers.put(component, new ErrorHandler(label));
			return prop.getChain(String.class).bind(value(component));
		}
		throw new IllegalStateException("Type not handled: " + prop.getEndOfChainType());
	}

	private JLabel addLabel(final PropertyEntry prop) {
		final GridBagConstraints labelConstraint = new GridBagConstraints();
		final JLabel label = addLabel(labelConstraint);
		label.setText(prop.getLabel());
		label.setToolTipText(prop.getTooltip());
		return label;
	}

	private JLabel addLabel(final GridBagConstraints labelConstraint) {
		final JLabel label = new JLabel();
		labelConstraint.gridx = 1;
		labelConstraint.gridy = currentRow;
		labelConstraint.insets = new Insets(5, 5, 0, 5);
		labelConstraint.anchor = GridBagConstraints.EAST;
		add(label, labelConstraint);
		return label;
	}

	private JSpinner addSpinner(final PropertyEntry prop) {
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

	private JTextField addTextField(final PropertyEntry prop) {
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
	public void build(final GenericEditorController<?> adapter, final ErrorSet errorProperty) {
		currentRow++;
		final GridBagConstraints labelConstraint = new GridBagConstraints();
		labelConstraint.gridwidth = 2;
		labelConstraint.fill = GridBagConstraints.BOTH;
		labelConstraint.weighty = 1.0;
		final JLabel errorLabel = addLabel(labelConstraint);
		errorLabel.setForeground(Color.RED.darker());
		errorProperty.getErrors().listenActive(e -> {
			errorHandlers.values().forEach(ErrorHandler::unsetError);
			e.keySet().stream().map(errorHandlers::get).filter(Objects::nonNull).forEach(ErrorHandler::setError);
		});
	}
}
